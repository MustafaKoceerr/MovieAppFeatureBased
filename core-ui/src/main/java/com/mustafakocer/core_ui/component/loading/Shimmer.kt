package com.mustafakocer.core_ui.component.loading

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Sonsuz bir shimmer animasyonu sağlayan bir Brush oluşturur.
 *
 * @param showShimmer Animasyonun aktif olup olmadığını kontrol eder.
 * @param targetValue Animasyonun ne kadar uzağa gideceğini belirler.
 * @return Shimmer efektini içeren bir Brush.
 */
@Composable
fun ShimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    if (!showShimmer) {
        return Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
    }

    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.0f),
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.0f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_animation"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )
}