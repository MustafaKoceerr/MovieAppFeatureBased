package com.mustafakocer.feature_splash.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustafakocer.feature_splash.presentation.contract.SplashEffect
import com.mustafakocer.feature_splash.presentation.viewmodel.SplashViewModel
import com.mustafakocer.navigation_contracts.actions.splash.SplashNavActions

/**
 * The main entry point and controller for the Splash screen feature.
 *
 * @param navActions An implementation of [SplashNavActions] for triggering navigation.
 * @param viewModel The ViewModel responsible for the Splash screen's logic.
 *
 * Architectural Note:
 * This Composable acts as the "route" or controller for the feature. Its primary responsibility
 * is to connect the ViewModel's side effects (`uiEffect`) to the navigation actions defined by
 * the `SplashNavActions` contract. This keeps the UI (`SplashScreen`) completely decoupled from
 * both the ViewModel and the navigation logic.
 */
@Composable
fun SplashRoute(
    navActions: SplashNavActions,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    // A LaunchedEffect observes the one-time navigation effects from the ViewModel.
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is SplashEffect.NavigateToHome -> navActions.navigateToHome()
                is SplashEffect.NavigateToWelcome -> navActions.navigateToWelcome()
            }
        }
    }

    // This is the "dumb" UI component that just displays the visual elements.
    SplashScreen()
}