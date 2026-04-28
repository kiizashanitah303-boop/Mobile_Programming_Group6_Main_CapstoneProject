package com.weatherapp.utils

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AnimationUtils {

    @Composable
    fun pulsate(scale: Float = 1f): Float {
        val infiniteTransition = rememberInfiniteTransition()
        val animatedScale by infiniteTransition.animateFloat(
            initialValue = scale,
            targetValue = scale + 0.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        return animatedScale
    }

    @Composable
    fun rotate(initialRotation: Float = 0f): Float {
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = initialRotation,
            targetValue = initialRotation + 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(20000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        return rotation
    }

    @Composable
    fun floatY(initialY: Float = 0f): Float {
        val infiniteTransition = rememberInfiniteTransition()
        val yOffset by infiniteTransition.animateFloat(
            initialValue = initialY,
            targetValue = initialY - 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        return yOffset
    }

    @Composable
    fun animatedGradient(colors: List<Color>): Brush {
        val infiniteTransition = rememberInfiniteTransition()
        val offset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(10000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        return Brush.linearGradient(
            colors = colors,
            start = Offset(offset, 0f),
            end = Offset(offset + 500f, 500f)
        )
    }
}
