package com.mustafakocer.movieappfeaturebasedclean.feature.auth.welcome.presentation.screen

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.welcome.presentation.viewmodel.WelcomeViewModel

@Composable
fun WelcomeRoute(
    onNavigateToHome: () -> Unit,
    viewModel: WelcomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.loginUrl) {
        state.loginUrl?.let { url ->
            context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
            viewModel.onLoginUrlHandled()
        }
    }

    LaunchedEffect(state.loggedIn) {
        if (state.loggedIn) onNavigateToHome()
    }

    WelcomeScreen(
        state = state,
        onLoginClick = viewModel::onLoginClick,
        onGuestClick = onNavigateToHome,
    )
}
