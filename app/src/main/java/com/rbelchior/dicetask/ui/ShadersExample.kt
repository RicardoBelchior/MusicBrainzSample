package com.rbelchior.dicetask.ui

import android.graphics.Bitmap
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


const val FRACTAL_SHADER_SRC = """
    uniform float2 size;
    uniform float time;
    uniform shader composable;
    
    float f(float3 p) {
        p.z -= time * 5.;
        float a = p.z * .1;
        p.xy *= mat2(cos(a), sin(a), -sin(a), cos(a));
        return .1 - length(cos(p.xy) + sin(p.yz));
    }
    
    half4 main(float2 fragcoord) { 
        float3 d = .5 - fragcoord.xy1 / size.y;
        float3 p=float3(0);
        for (int i = 0; i < 32; i++) {
          p += f(p) * d;
        }
        return ((sin(p) + float3(2, 5, 12)) / length(p)).xyz1;
    }
"""

const val IMG_SHADER_SRC = """
    uniform float2 size;
    uniform float time;
    uniform shader composable;
    
    half4 main(float2 fragCoord) {
        float scale = 1 / size.x;
        float2 scaledCoord = fragCoord * scale;
        float2 center = size * 0.5 * scale;
        float dist = distance(scaledCoord, center);
        float2 dir = scaledCoord - center;
        float sin = sin(dist * 70 - time * 6.28);
        float2 offset = dir * sin;
        float2 textCoord = scaledCoord + offset / 30;
        return composable.eval(textCoord / scale);
    }
"""


@Composable
fun ShaderExample(
    photo: Bitmap,
    modifier: Modifier = Modifier
) {
    val shader = remember { RuntimeShader(IMG_SHADER_SRC) }
    val scope = rememberCoroutineScope()
    val timeMs = remember { mutableStateOf(0f) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Image(
            bitmap = photo.asImageBitmap(),
            modifier = Modifier
                .onSizeChanged { size ->
                    shader.setFloatUniform(
                        "size",
                        size.width.toFloat(),
                        size.height.toFloat()
                    )
                }
                .graphicsLayer {
                    clip = true
                    shader.setFloatUniform("time", timeMs.value)

                    renderEffect =
                        RenderEffect
                            .createRuntimeShaderEffect(shader, "composable")
                            .asComposeRenderEffect()
                },
            contentScale = ContentScale.FillHeight,
            contentDescription = null,
        )
    }

    LaunchedEffect(Unit) {
        scope.launch {
            while (true) {
                timeMs.value = (System.currentTimeMillis() % 100_000L) / 1_000f
                delay(10)
            }
        }
    }
}

const val CRT_SHADER_SRC = """
    uniform float2 size;
    uniform shader composable;
    
    // Constants from the ShaderToy example
    const float warp = 0.25; // simulate curvature of CRT monitor
    const float scan = 0.75; // simulate darkness between scanlines
    
    half4 main(float2 fragCoord) {
        // 1. Normalize coordinates to 0.0 - 1.0 (UV space)
        float2 uv = fragCoord / size;
        
        // 2. Calculate distance from center (squared)
        float2 dc = abs(0.5 - uv);
        dc *= dc;
        
        // 3. Warp the fragment coordinates (Barrel distortion)
        uv.x -= 0.5; uv.x *= 1.0 + (dc.y * (0.3 * warp)); uv.x += 0.5;
        uv.y -= 0.5; uv.y *= 1.0 + (dc.x * (0.4 * warp)); uv.y += 0.5;
        
        // 4. Sample inside boundaries, otherwise set to black
        if (uv.y > 1.0 || uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0) {
            return half4(0.0, 0.0, 0.0, 1.0);
        } else {
            // 5. Determine if we are drawing in a scanline
            // We use fragCoord.y (pixels) for the sine wave to keep 1:1 pixel fidelity
            float apply = abs(sin(fragCoord.y) * 0.5 * scan);
            
            // 6. Sample the texture
            // CRITICAL: composable.eval expects PIXEL coordinates, not UVs. 
            // We multiply uv * size to get back to pixels.
            half4 texColor = composable.eval(uv * size);
            
            // 7. Apply the dark scanline mix
            return half4(mix(texColor.rgb, half3(0.0), apply), 1.0);
        }
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU) // RuntimeShader requires API 33+
@Composable
fun CrtShaderExample(
    photo: Bitmap,
    modifier: Modifier = Modifier
) {
    // Compile the shader once
    val shader = remember { RuntimeShader(CRT_SHADER_SRC) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Image(
            bitmap = photo.asImageBitmap(),
            contentScale = ContentScale.FillBounds, // Ensure it fills to apply effect to whole screen
            contentDescription = null,
            modifier = Modifier
                .onSizeChanged { size ->
                    // Update resolution input
                    shader.setFloatUniform(
                        "size",
                        size.width.toFloat(),
                        size.height.toFloat()
                    )
                }
                .graphicsLayer {
                    clip = true
                    // Attach the shader as a RenderEffect
                    // "composable" matches the uniform shader name in the AGSL string
                    renderEffect = RenderEffect
                        .createRuntimeShaderEffect(shader, "composable")
                        .asComposeRenderEffect()
                }
        )
    }
}

const val PARTICLE_SHADER_SRC = """
    uniform float2 iResolution;
    uniform float iTime;
    uniform shader composable;

    float4 main(float2 fragCoord) {
        float2 u = fragCoord * 2.0 - iResolution;
        float2 p = float2(0.0);
        float2 c = float2(0.0);
        float a = 0.0;
        
        // Accumulator
        float4 o = float4(0.0);
        
        // Loop 400 times. 
        // Optimization: Reduce to 200.0 if frames drop on older devices.
        for (float i = 0.0; i < 400.0; i += 1.0) {
            a = i / 200.0 - 1.0;
            p = cos(i * 2.4 + iTime + float2(0.0, 11.0)) * sqrt(1.0 - a * a);
            c = u / iResolution.y + float2(p.x, a) / (p.y + 2.0);
            
            float intensity = (1.0 - p.y) / 30000.0;
            float4 colorBase = cos(i + float4(0.0, 2.0, 4.0, 0.0)) + 1.0;
            
            // Additive blending
            // We use max() to prevent division by zero for pixels exactly at the center of a star
            o += colorBase / max(dot(c, c), 0.00001) * intensity;
        }
        
        // Return opaque color
        return float4(o.rgb, 1.0);
    }

"""


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ParticleShaderExample(
    modifier: Modifier = Modifier
) {
    val shader = remember { RuntimeShader(PARTICLE_SHADER_SRC) }
    // We use a state to hold time.
    // For high-performance animation, usually `withFrameNanos` is preferred,
    // but this approach is simpler for drop-in usage.
    val timeState = remember { mutableFloatStateOf(0f) }

    // Run the animation loop
    LaunchedEffect(Unit) {
        val startTime = System.nanoTime()
        while (true) {
            // Convert nanoseconds to seconds for iTime
            timeState.floatValue = (System.nanoTime() - startTime) / 1_000_000_000f
            withFrameNanos { } // Wait for next frame
        }
    }

    val shaderBrush = remember(shader) { ShaderBrush(shader) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black) // Fallback background
            .drawBehind {
                // 1. Pass the resolution (size of the drawing area)
                shader.setFloatUniform("iResolution", size.width, size.height)

                // 2. Pass the time
                shader.setFloatUniform("iTime", timeState.floatValue)

                // 3. Draw the shader over the entire area
                drawRect(brush = shaderBrush)
            }

//            .onSizeChanged { size ->
//                shader.setFloatUniform(
//                    "iResolution",
//                    size.width.toFloat(),
//                    size.height.toFloat()
//                )
//            }
//            .graphicsLayer {
//                shader.setFloatUniform("iTime", timeState.floatValue)
//                renderEffect = RenderEffect
//                    .createRuntimeShaderEffect(shader, "composable") // Name is ignored as we don't use input
//                    .asComposeRenderEffect()
//                clip = true
//            }
    )
}


//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        val shader = RuntimeShader(IMG_SHADER_SRC)
////        val shader = RuntimeShader(FRACTAL_SHADER_SRC) // TODO: uncomment to see 2nd shader
//        val photo: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.butterfly)
//
//        setContent {
//            val scope = rememberCoroutineScope()
//            val timeMs = remember { mutableStateOf(0f) }
//            LaunchedEffect(Unit) {
//                scope.launch {
//                    while (true) {
//                        timeMs.value = (System.currentTimeMillis() % 100_000L) / 1_000f
//                        delay(10)
//                    }
//                }
//            }
//
//        }
//    }
//}
