package com.mustafakocer.movieappfeaturebasedclean.feature.splash.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.movieappfeaturebasedclean.feature.splash.presentation.viewmodel.SplashDestination
import com.mustafakocer.movieappfeaturebasedclean.feature.splash.presentation.viewmodel.SplashViewModel

@Composable
fun SplashRoute(
    onNavigateToHome: () -> Unit,
    onNavigateToWelcome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.destination) {
        when (state.destination) {
            SplashDestination.Home -> onNavigateToHome()
            SplashDestination.Welcome -> onNavigateToWelcome()
            null -> Unit
        }
    }

    SplashScreen()
}
