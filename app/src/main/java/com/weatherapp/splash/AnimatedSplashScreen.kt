package com.weatherapp.splash


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.weatherapp.R
import kotlinx.coroutines.delay

@Composable
fun AnimatedSplashScreen(
    navController: NavController,
    onTimeout: () -> Unit
) {
    // Animation states
    var startAnimation by remember { mutableStateOf(false) }
    var iconScale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var textAlpha by remember { mutableStateOf(0f) }
    var loadingProgress by remember { mutableStateOf(0f) }

    // Animated gradient background
    val animatedBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary
        )
    )

    // Icon scaling animation
    val scaleAnimated by animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 1f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )

    // Rotation animation
    val rotationAnimated by animateFloatAsState(
        targetValue = if (startAnimation) 360f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = LinearEasing
        ),
        label = "rotation"
    )

    // Text fade animation
    val textFade by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000),
        label = "fade"
    )

    // Loading progress animation
    val progress by animateFloatAsState(
        targetValue = loadingProgress,
        animationSpec = tween(2500),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(500)
        loadingProgress = 1f
        delay(2000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = animatedBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated Weather Icon
            Image(
                painter = painterResource(id = R.drawable.ic_splash_weather),
                contentDescription = "Weather App Icon",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scaleAnimated)
                    .rotate(rotationAnimated)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated App Name
            AnimatedVisibility(
                visible = textFade > 0f,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    text = "WeatherWise",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.alpha(textFade)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline with animation
            AnimatedVisibility(
                visible = textFade > 0.5f,
                enter = fadeIn(animationSpec = tween(500, delayMillis = 500)) +
                        slideInHorizontally(),
                exit = fadeOut()
            ) {
                Text(
                    text = "Your Weather Companion",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    modifier = Modifier.alpha(textFade)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator with animation
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = MaterialTheme.shapes.small
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bouncing dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.alpha(if (progress < 1f) 1f else 0f)
            ) {
                repeat(3) { index ->
                    val bounce by animateFloatAsState(
                        targetValue = if (progress < 1f) {
                            if (index == 0) 1.2f else 1f
                        } else 0f,
                        animationSpec = repeatable(
                            iterations = Int.MAX_VALUE,
                            animation = tween(500, easing = EaseInOut),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "bounce_$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(bounce)
                            .background(
                                color = MaterialTheme.colorScheme.onPrimary,
                                shape = MaterialTheme.shapes.small
                            )
                    )
                }
            }
        }
    }
}
