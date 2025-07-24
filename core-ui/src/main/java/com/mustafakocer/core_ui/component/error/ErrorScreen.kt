package com.mustafakocer.core_ui.component.error

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme

/**
 * A standardized, reusable Composable for displaying a full-screen error state.
 *
 * @param modifier The modifier to be applied to the component.
 * @param error The [ErrorInfo] data object that dictates the content to be displayed.
 * @param onRetry An optional lambda to be invoked when the user clicks the retry button.
 * @param onNavigateBack An optional lambda to be invoked when the user clicks the "Go Back" button.
 *
 * Architectural Note:
 * This component is a key part of a robust UI strategy. It's a "dumb" component that is
 * driven entirely by the [ErrorInfo] data class. This decouples the error presentation from
 * the business logic that generates the error. Any screen can display a consistent, themed
 * error message simply by providing this data, promoting reusability and a uniform user experience.
 */
@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    error: ErrorInfo,
    onRetry: (() -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "error_animation")

    val bounceAnimation by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.scale(bounceAnimation)
                ) {
                    Icon(
                        imageVector = error.icon,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = error.emoji,
                        fontSize = 32.sp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 8.dp, y = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = error.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = error.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                if (onRetry != null || onNavigateBack != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                    ) {
                        onNavigateBack?.let {
                            OutlinedButton(
                                onClick = it,
                                modifier = Modifier
                                    .weight(1f)
                                    .bounceClick()
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Go Back")
                            }
                        }
                        onRetry?.let {
                            Button(
                                onClick = it,
                                modifier = Modifier
                                    .weight(1f)
                                    .bounceClick()
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text(error.retryText)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    MovieDiscoveryTheme {
        val errorInfo = AppException.Network.NoInternet().toErrorInfo()
        ErrorScreen(
            error = errorInfo,
            onRetry = {},
            onNavigateBack = {}
        )
    }
}