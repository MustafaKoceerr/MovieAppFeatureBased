package com.mustafakocer.feature_auth.presentation.welcome.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.mustafakocer.feature_auth.presentation.welcome.components.WelcomeContent
import com.mustafakocer.feature_auth.presentation.welcome.contract.WelcomeEvent
import com.mustafakocer.feature_auth.presentation.welcome.contract.WelcomeUiState

@Composable
fun WelcomeScreen(
    state: WelcomeUiState,
    onEvent: (WelcomeEvent) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        WelcomeContent(
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
