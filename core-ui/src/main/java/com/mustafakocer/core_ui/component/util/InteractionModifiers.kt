package com.mustafakocer.core_ui.component.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * A custom Modifier that adds a physical "bounce" effect to a Composable on press.
 *
 * @return A Modifier instance that applies the bounce effect.
 *
 * Architectural Note:
 * This Modifier provides a consistent and reusable way to add satisfying physical feedback to any
 * clickable component, enhancing the user experience.
 *
 * - **Why `composed`?** It uses the `composed` factory to create a stateful modifier, ensuring
 *   that each Composable using it maintains its own independent press state.
 * - **Why `pointerInput`?** Low-level touch events are handled with `pointerInput` to precisely
 *   control the `isPressed` state that drives the animation, offering more control than relying
 *   solely on `InteractionSource`.
 * - **Why `clickable(indication = null)`?** The standard ripple `indication` is explicitly
 *   disabled to allow the custom scale animation to be the sole visual feedback for the interaction,
 *   preventing visual clutter. The `onClick` lambda is empty because the actual click handling
 *   is deferred to the component this modifier is applied to (e.g., a `Button`'s own `onClick`).
 */
fun Modifier.bounceClick(): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "bounce_scale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { /* No-op; handled by the component's own click listener */ }
        )
        .pointerInput(isPressed) {
            awaitPointerEventScope {
                isPressed = if (isPressed) {
                    waitForUpOrCancellation()
                    false
                } else {
                    awaitFirstDown(requireUnconsumed = false)
                    true
                }
            }
        }
}