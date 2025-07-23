package com.mustafakocer.feature_auth.welcome.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.mustafakocer.feature_auth.welcome.presentation.components.WelcomeContent
import com.mustafakocer.feature_auth.welcome.presentation.contract.WelcomeEvent
import com.mustafakocer.feature_auth.welcome.presentation.contract.WelcomeUiState

/**
 * A purely visual, "dumb" component that displays the main welcome UI.
 *
 * @param state The current UI state to render.
 * @param onEvent A lambda to propagate user interactions up to the ViewModel.
 *
 * Architectural Note:
 * This Composable acts as a simple container and theming wrapper. Its primary responsibility
 * is to delegate the actual UI rendering to more specific components like `WelcomeContent`.
 * It remains stateless and is driven entirely by the `state` object, forwarding all user
 * actions through the `onEvent` callback. This separation of concerns makes the UI hierarchy
 * cleaner and easier to manage.
 */
@Composable
fun WelcomeScreen(
    state: WelcomeUiState,
    onEvent: (WelcomeEvent) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        WelcomeContent(
            state = state,
            onLoginClick = { onEvent(WelcomeEvent.LoginClicked) },
            onGuestClick = { onEvent(WelcomeEvent.GuestClicked) },
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )
    }
}