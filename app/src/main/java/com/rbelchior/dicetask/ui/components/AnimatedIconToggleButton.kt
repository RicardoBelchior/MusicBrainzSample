package com.rbelchior.dicetask.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedIconToggleButton(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    IconToggleButton(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
    ) {
        // on below line we are creating a
        // variable for our transition
        val transition = updateTransition(isChecked, label = "")

        // on below line we are creating a variable for
        // color of our icon
        val tint by transition.animateColor(label = "iconColor") { isChecked ->
            // if toggle button is checked we are setting color as red.
            // in else condition we are setting color as black
            if (isChecked) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        }

        // om below line we are specifying transition
        val size by transition.animateDp(
            transitionSpec = {
                // on below line we are specifying transition
                if (false isTransitioningTo true) {
                    // on below line we are specifying key frames
                    keyframes {
                        // on below line we are specifying animation duration
                        durationMillis = 250
                        // on below line we are specifying animations.
                        30.dp at 0 with LinearOutSlowInEasing // for 0-15 ms
                        35.dp at 15 with FastOutLinearInEasing // for 15-75 ms
                        40.dp at 75 // ms
                        35.dp at 150 // ms
                    }
                } else {
                    spring(stiffness = Spring.StiffnessVeryLow)
                }
            },
            label = "Size"
        ) { _ -> 30.dp }

        // on below line we are creating icon for our toggle button.
        Icon(
            // on below line we are specifying icon for our image vector.
            imageVector = if (isChecked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = "Icon",
            // on below line we are specifying
            // tint for our icon.
            tint = tint,
            // on below line we are specifying
            // size for our icon.
            modifier = androidx.compose.ui.Modifier.size(size)
        )
    }
}
