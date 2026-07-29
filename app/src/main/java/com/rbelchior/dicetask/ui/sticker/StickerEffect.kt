package com.rbelchior.dicetask.ui.sticker

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rbelchior.dicetask.R
import com.rbelchior.dicetask.ui.theme.DiceTaskTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min

/**
 * Android/Compose port of https://github.com/bpisano/Sticker
 *
 * Applies a Pokemon-style holographic foil effect to any composable using an AGSL [RuntimeShader].
 * The AGSL below is a faithful translation of the upstream Metal shaders (`FoilShader.metal` and
 * `ReflectionShader.metal`): a metallic gradient modulated by a checker pattern and value noise,
 * with a soft specular reflection that tracks the pointer/drag position.
 *
 * Requires API 33+ (Tiramisu) because [RuntimeShader] is only available there.
 */

/** Checker pattern used by the foil, mirroring Sticker's `StickerPatternType`. */
enum class StickerPattern(val value: Int) {
    Diamond(0),
    Square(1),
}

/** How the effect reacts to input, mirroring Sticker's motion effects. */
enum class StickerMotionType {
    /** Reacts to mouse/stylus hover (desktop/ChromeOS/tablet with pointer). */
    PointerHover,

    /** Reacts to touch press + drag. Best default on phones. */
    Drag,

    /** Reacts to the device's orientation (rotation vector) sensor, like tilting a foil card. */
    Accelerometer,
}

private val MoveSpec = spring<Float>(
    dampingRatio = 0.9f,
    stiffness = Spring.StiffnessMediumLow,
)

private val ReleaseSpec = spring<Float>(
    dampingRatio = 0.75f,
    stiffness = Spring.StiffnessLow,
)

// Critically damped follow spring used to smooth out sensor jitter for the accelerometer.
private val TiltSpec = spring<Float>(
    dampingRatio = 1f,
    stiffness = Spring.StiffnessMedium,
)

/**
 * Holds the animated interaction state for the sticker effect. The transform is stored in
 * "centered pixels" (0,0 == view center), matching the coordinate space used by Sticker.
 */
class StickerMotionState internal constructor(private val scope: CoroutineScope) {
    internal val x = Animatable(0f)
    internal val y = Animatable(0f)

    /** 0 when at rest, 1 while the user is interacting. Drives the reflection intensity. */
    internal val activation = Animatable(0f)

    // Latest measured content size (px). Needed to convert sensor tilt into centered pixels.
    internal var widthPx: Float = 0f
    internal var heightPx: Float = 0f

    internal fun updateSize(width: Float, height: Float) {
        widthPx = width
        heightPx = height
    }

    internal fun onMove(centeredX: Float, centeredY: Float) {
        scope.launch { x.animateTo(centeredX, MoveSpec) }
        scope.launch { y.animateTo(centeredY, MoveSpec) }
        scope.launch { activation.animateTo(1f, MoveSpec) }
    }

    internal fun onRelease() {
        scope.launch { x.animateTo(0f, ReleaseSpec) }
        scope.launch { y.animateTo(0f, ReleaseSpec) }
        scope.launch { activation.animateTo(0f, ReleaseSpec) }
    }

    /**
     * Feed a device tilt (in radians) from the orientation sensor. Mirrors Sticker's accelerometer
     * effect: the transform is `rotation * size / 2` so the foil offset and reflection track the
     * same coordinate space as pointer/drag input.
     */
    internal fun onTilt(xRotationRad: Float, yRotationRad: Float) {
        scope.launch { x.animateTo(xRotationRad * widthPx / 2f, TiltSpec) }
        scope.launch { y.animateTo(yRotationRad * heightPx / 2f, TiltSpec) }
        scope.launch { activation.animateTo(1f, MoveSpec) }
    }
}

@Composable
fun rememberStickerMotionState(): StickerMotionState {
    val scope = rememberCoroutineScope()
    return remember { StickerMotionState(scope) }
}

/**
 * Applies the holographic sticker effect. Chain the customization parameters just like the
 * SwiftUI modifiers (`.stickerScale`, `.stickerColorIntensity`, ...).
 *
 * @param motion interaction state, create with [rememberStickerMotionState].
 * @param motionType which input drives the effect ([StickerMotionType.Drag] by default).
 * @param motionIntensity strength of the tilt/motion that drives the effect (Sticker's `intensity`).
 * @param maxRotation soft ceiling (in degrees) for the accelerometer tilt (Sticker's `maxRotation`).
 * @param shape shape used to clip the result (e.g. rounded corners for a card look).
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Modifier.stickerEffect(
    motion: StickerMotionState,
    motionType: StickerMotionType = StickerMotionType.Drag,
    motionIntensity: Float = 1f,
    maxRotation: Float = 90f,
    scale: Float = 3f,
    colorIntensity: Float = 0.8f,
    contrast: Float = 0.9f,
    checkerScale: Float = 5f,
    checkerIntensity: Float = 1.2f,
    noiseScale: Float = 100f,
    noiseIntensity: Float = 1.2f,
    lightIntensity: Float = 0.3f,
    pattern: StickerPattern = StickerPattern.Diamond,
    shape: Shape = RectangleShape,
): Modifier {
    val shader = remember { RuntimeShader(STICKER_SHADER_SRC) }

    if (motionType == StickerMotionType.Accelerometer) {
        AccelerometerMotionEffect(motion, motionIntensity, maxRotation)
    }

    return this
        .onSizeChanged { motion.updateSize(it.width.toFloat(), it.height.toFloat()) }
        .then(
            when (motionType) {
                StickerMotionType.Accelerometer -> Modifier
                StickerMotionType.Drag -> Modifier.pointerInput(motion) {
                    val center = { Offset(size.width / 2f, size.height / 2f) }
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val c = center()
                        motion.onMove(down.position.x - c.x, down.position.y - c.y)
                        drag(down.id) { change ->
                            motion.onMove(change.position.x - c.x, change.position.y - c.y)
                        }
                        motion.onRelease()
                    }
                }

                StickerMotionType.PointerHover -> Modifier.pointerInput(motion) {
                    val center = { Offset(size.width / 2f, size.height / 2f) }
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val c = center()
                            when (event.type) {
                                PointerEventType.Enter, PointerEventType.Move -> {
                                    val p = event.changes.last().position
                                    motion.onMove(p.x - c.x, p.y - c.y)
                                }

                                PointerEventType.Exit -> motion.onRelease()
                            }
                        }
                    }
                }
            }
        )
        .graphicsLayer {
            val w = size.width
            val h = size.height
            if (w <= 0f || h <= 0f) return@graphicsLayer

            val tx = motion.x.value
            val ty = motion.y.value
            val active = motion.activation.value

            shader.setFloatUniform("size", w, h)
            // Foil offset: pixel-centered transform scaled by -150, like Sticker.
            shader.setFloatUniform("offset", tx * -150f, ty * -150f)
            shader.setFloatUniform("scale", scale)
            shader.setFloatUniform("intensity", colorIntensity)
            shader.setFloatUniform("contrast", contrast)
            shader.setFloatUniform("checkerScale", checkerScale)
            shader.setFloatUniform("checkerIntensity", checkerIntensity)
            shader.setFloatUniform("noiseScale", noiseScale)
            shader.setFloatUniform("noiseIntensity", noiseIntensity)
            shader.setFloatUniform("patternType", pattern.value.toFloat())
            shader.setFloatUniform(
                "reflectionPosition",
                (tx + w / 2f) / w,
                (ty + h / 2f) / h,
            )
            shader.setFloatUniform("reflectionSize", min(w, h) / 2f)
            shader.setFloatUniform("reflectionIntensity", lightIntensity * active)

            // 3D tilt that looks toward the pointer (Sticker's rotation3DEffect).
            rotationY = Math.toDegrees(((tx / w) * motionIntensity).toDouble()).toFloat()
            rotationX = Math.toDegrees(((-ty / h) * motionIntensity).toDouble()).toFloat()
            cameraDistance = 14f * density

            this.shape = shape
            clip = true
            renderEffect = RenderEffect
                .createRuntimeShaderEffect(shader, "composable")
                .asComposeRenderEffect()
        }
}

/**
 * Registers the device orientation (rotation vector) sensor while composed and feeds a
 * reference-relative, softly-clamped tilt into [motion]. Port of Sticker's accelerometer effect.
 *
 * @param intensity multiplies the measured tilt (Sticker's `intensity`).
 * @param maxRotation soft ceiling in degrees; tilt asymptotically approaches but never exceeds it.
 */
@Composable
private fun AccelerometerMotionEffect(
    motion: StickerMotionState,
    intensity: Float,
    maxRotation: Float,
) {
    val context = LocalContext.current
    DisposableEffect(motion, intensity, maxRotation) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (sensorManager == null || sensor == null) {
            return@DisposableEffect onDispose { }
        }

        val maxRad = Math.toRadians(maxRotation.toDouble()).toFloat()
        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)
        var referencePitch: Float? = null
        var referenceRoll: Float? = null

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientation)
                // orientation = [azimuth, pitch, roll] in radians.
                val pitch = orientation[1]
                val roll = orientation[2]

                if (referencePitch == null || referenceRoll == null) {
                    referencePitch = pitch
                    referenceRoll = roll
                }

                val relPitch = pitch - referencePitch!!
                val relRoll = roll - referenceRoll!!

                val xRotation = diminishingRotation(relRoll * intensity, maxRad)
                val yRotation = diminishingRotation(relPitch * intensity, maxRad)
                motion.onTilt(xRotation, yRotation)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        sensorManager.registerListener(listener, sensor, ACCELEROMETER_UPDATE_MICROS)
        onDispose {
            sensorManager.unregisterListener(listener)
            motion.onRelease()
        }
    }
}

// Soft clamp so tilt asymptotically approaches maxRotation without ever exceeding it.
private fun diminishingRotation(tilt: Float, maxRad: Float): Float {
    val scale = 1f / (1f + abs(tilt) / maxRad)
    return tilt * scale
}

// ~50 Hz, matching Sticker's default 0.02s update interval.
private const val ACCELEROMETER_UPDATE_MICROS = 20_000

private const val STICKER_SHADER_SRC = """
    uniform shader composable;
    uniform float2 size;
    uniform float2 offset;
    uniform float scale;
    uniform float intensity;
    uniform float contrast;
    uniform float checkerScale;
    uniform float checkerIntensity;
    uniform float noiseScale;
    uniform float noiseIntensity;
    uniform float patternType;
    uniform float2 reflectionPosition;
    uniform float reflectionSize;
    uniform float reflectionIntensity;

    const float PI = 3.14159265359;

    float random(float2 uv) {
        return fract(sin(dot(uv, float2(12.9898, 78.233))) * 43758.5453);
    }

    float calculateBrightness(half4 color) {
        return color.r * 0.299 + color.g * 0.587 + color.b * 0.114;
    }

    float noisePattern(float2 uv) {
        float2 i = floor(uv);
        float2 f = fract(uv);
        float a = random(i);
        float b = random(i + float2(1.0, 0.0));
        float c = random(i + float2(0.0, 1.0));
        float d = random(i + float2(1.0, 1.0));
        float2 u = smoothstep(0.0, 1.0, f);
        return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
    }

    // Mix colors with more intensity on lighter areas, keeping a baseline for dark ones.
    half4 lightnessMix(half4 baseColor, half4 overlayColor, float strength, float baselineFactor) {
        float brightness = calculateBrightness(baseColor);
        float adjustedMixFactor = max(smoothstep(0.2, 1.0, brightness) * strength, baselineFactor);
        return mix(baseColor, overlayColor, half(adjustedMixFactor));
    }

    // Increase contrast based on a pattern value, weighted by brightness.
    half4 increaseContrast(half4 source, float pattern, float strength) {
        float brightness = calculateBrightness(source);
        float contrastFactor = mix(1.0, strength, pattern * brightness);
        return (source - half4(0.5)) * half(contrastFactor) + half4(0.5);
    }

    float squarePattern(float2 uv, float s, float degreesAngle) {
        float radiansAngle = degreesAngle * PI / 180.0;
        uv *= s;
        float cosAngle = cos(radiansAngle);
        float sinAngle = sin(radiansAngle);
        float2 rotatedUV = float2(
            cosAngle * uv.x - sinAngle * uv.y,
            sinAngle * uv.x + cosAngle * uv.y
        );
        return mod(floor(rotatedUV.x) + floor(rotatedUV.y), 2.0) == 0.0 ? 0.0 : 1.0;
    }

    float diamondPattern(float2 uv, float s) {
        return squarePattern(uv, s, 45.0);
    }

    float stickerPattern(int option, float2 uv, float s) {
        if (option == 1) {
            return squarePattern(uv, s, 0.0);
        }
        return diamondPattern(uv, s);
    }

    // Soft specular highlight that tracks the pointer (ReflectionShader.metal).
    half4 reflection(half4 color, float2 position) {
        float2 uv = position / size;
        float d = distance(uv, reflectionPosition);
        float blurFactor = smoothstep(reflectionSize / size.x, 0.0, d);
        half4 reflectionColor = half4(1.0, 1.0, 1.0, half(reflectionIntensity * blurFactor));
        return mix(color, reflectionColor, reflectionColor.a);
    }

    // Holographic metallic foil (FoilShader.metal).
    half4 foil(half4 color, float2 position) {
        float aspectRatio = size.x / size.y;
        float2 normalizedOffset = (offset + size * 250.0) / (size * scale) * 0.01;
        float2 normalizedPosition = float2(position.x * aspectRatio, position.y);
        float2 uv = (position / (size * scale)) + normalizedOffset;

        float gradientNoise = random(position) * 0.1;
        float pattern = stickerPattern(
            int(patternType),
            normalizedPosition / size * checkerScale,
            checkerScale
        );
        float noise = noisePattern(position / size * noiseScale);

        half r = half(contrast + 0.25 * sin(uv.x * 10.0 + gradientNoise));
        half g = half(contrast + 0.25 * cos(uv.y * 10.0 + gradientNoise));
        half b = half(contrast + 0.25 * sin((uv.x + uv.y) * 10.0 - gradientNoise));

        half4 foilColor = half4(r, g, b, 1.0);
        half4 mixedFoilColor = lightnessMix(color, foilColor, intensity, 0.3);
        half4 checkerFoil = increaseContrast(mixedFoilColor, pattern, checkerIntensity);
        half4 noiseCheckerFoil = increaseContrast(checkerFoil, noise, noiseIntensity);
        return noiseCheckerFoil;
    }

    half4 main(float2 fragCoord) {
        half4 src = composable.eval(fragCoord);
        half4 reflected = reflection(src, fragCoord);
        half4 result = foil(reflected, fragCoord);
        // Preserve the original silhouette/alpha, like SwiftUI's .mask(content).
        return half4(result.rgb, 1.0) * src.a;
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun StickerDemoScreen(modifier: Modifier = Modifier) {
    val motion = rememberStickerMotionState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E12)),
        contentAlignment = Alignment.Center,
    ) {

        Column(
            modifier = Modifier

                .stickerEffect(
                    motion = motion,
                    motionType = StickerMotionType.Accelerometer,
                    maxRotation = 90f,

//                    scale = 3f,
//                    colorIntensity = 0.8f,
//                    contrast = 0.9f,
//                    checkerScale = 5f,
//                    checkerIntensity = 1.2f,
//                    noiseScale = 100f,
//                    noiseIntensity = 1.2f,
//                    lightIntensity = 0.3f,

                    shape = RoundedCornerShape(24.dp),
                )
                .background(color = Color.LightGray, shape = RoundedCornerShape(15.dp))
                .padding(10.dp)
                .dashedBorder(
                    strokeWidth = 1.dp,
                    color = Color.Black.copy(alpha = 0.3f),
                    cornerRadiusDp = 15.dp
                )
        ) {

            Image(
                painter = painterResource(R.drawable.the_fan),
                contentDescription = "Holographic sticker. Drag over it to reveal the foil.",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(250.dp),
            )
            Text(
                text = "DICE",
                color = Color.Black,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
            )
        }

    }
}

fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadiusDp: Dp) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }
        val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }

        this.then(
            Modifier.drawWithCache {
                onDrawBehind {
                    val stroke = Stroke(
                        width = strokeWidthPx,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                    )

                    drawRoundRect(
                        color = color,
                        style = stroke,
                        cornerRadius = CornerRadius(cornerRadiusPx)
                    )
                }
            }
        )
    }
)


@Preview(showBackground = true)
@Composable
private fun StickerDemoPreview() {
    DiceTaskTheme {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            StickerDemoScreen()
        }
    }
}
