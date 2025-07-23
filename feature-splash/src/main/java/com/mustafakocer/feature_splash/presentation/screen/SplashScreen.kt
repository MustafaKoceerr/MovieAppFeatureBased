package com.mustafakocer.feature_splash.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.feature_splash.R
import com.mustafakocer.core_ui.R.drawable.img_app_logo as appLogo

@Composable
fun SplashScreen() {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "logo_scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "logo_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    ),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animasyonlu Logo (Icon yerine Image kullanıyoruz)
            Image(
                painter = painterResource(id = appLogo),
                contentDescription = stringResource(R.string.app_logo),
                modifier = Modifier
                    .size(240.dp)
                    .scale(scale)
                    .alpha(alpha),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_name_one),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.alpha(alpha)
            )


            Spacer(modifier = Modifier.height(48.dp))

            LoadingDotsIndicator()
        }
    }
}

@Composable
private fun LoadingDotsIndicator() {
    val dots = listOf(0, 1, 2)
    val transition = rememberInfiniteTransition(label = "dots_transition")

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        dots.forEach { index ->
            val yOffset by transition.animateFloat(
                initialValue = 0f,
                targetValue = -10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 300,
                        delayMillis = index * 100, // Her nokta bir sonrakinden 100ms sonra başlar
                        easing = LinearOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_offset"
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .offset(y = yOffset.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    MovieDiscoveryTheme(theme = ThemePreference.DARK) {
        SplashScreen()
    }
}