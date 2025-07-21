package com.mustafakocer.feature_auth.presentation.welcome.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.feature_auth.R
import com.mustafakocer.feature_auth.presentation.welcome.contract.WelcomeUiState
import com.mustafakocer.feature_auth.presentation.welcome.screen.WelcomeScreen


@Composable
fun WelcomeContent(
    state: WelcomeUiState,
    onLoginClick: () -> Unit,
    onGuestClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo
        AppLogo()
        Spacer(modifier = Modifier.height(48.dp))

        // Title and Subtitle
        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(64.dp))

        // Action Buttons
// Action Buttons
        LoginButton(
            onClick = onLoginClick,
            isLoading = state.isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))
        GuestButton(
            onClick = onGuestClick,
            isLoading = state.isLoading
        )
    }
}

@Composable
private fun AppLogo(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Filled.Theaters,
        contentDescription = stringResource(R.string.app_logo),
        modifier = modifier.size(120.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun LoginButton(
    onClick: () -> Unit,
    isLoading: Boolean, // Yükleme durumunu al
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = !isLoading, // Yükleniyorsa butonu devre dışı bırak
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(
                text = stringResource(R.string.login_with_tmdb),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GuestButton(
    onClick: () -> Unit,
    isLoading: Boolean, // Yükleme durumunu al
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        enabled = !isLoading, // Yükleniyorsa butonu devre dışı bırak
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.continue_as_guest),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WelcomeScreenPreview() {

    MovieDiscoveryTheme {
        WelcomeScreen(WelcomeUiState(), {})
    }
}

