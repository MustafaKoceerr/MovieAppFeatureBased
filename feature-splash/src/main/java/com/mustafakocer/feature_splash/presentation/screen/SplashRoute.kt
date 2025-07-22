package com.mustafakocer.feature_splash.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustafakocer.feature_splash.presentation.contract.SplashEffect
import com.mustafakocer.feature_splash.presentation.viewmodel.SplashViewModel
import com.mustafakocer.navigation_contracts.actions.splash.SplashNavActions

@Composable
fun SplashRoute(
    navActions: SplashNavActions,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is SplashEffect.NavigateToHome -> navActions.navigateToHome()
                is SplashEffect.NavigateToWelcome -> navActions.navigateToWelcome()
            }
        }
    }

    SplashScreen()
}