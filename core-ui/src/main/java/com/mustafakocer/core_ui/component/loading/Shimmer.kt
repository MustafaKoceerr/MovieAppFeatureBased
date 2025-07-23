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
 * Creates a `Brush` that produces a shimmering animation effect.
 *
 * @param showShimmer A flag to enable or disable the shimmer effect.
 * @param targetValue The end value for the animation's translation, controlling the sweep distance.
 * @return A `Brush` instance that can be used for drawing.
 *
 * Architectural Note:
 * This function encapsulates the complex logic of `rememberInfiniteTransition` to provide a
 * simple, reusable API for applying a shimmer effect. By returning a `Brush`, it remains
 * highly flexible and can be applied to any Composable that accepts a brush for its background
 * or drawing. The `showShimmer` parameter provides a clean and efficient way to conditionally
 * disable the animation entirely, avoiding unnecessary composition and animation overhead.
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