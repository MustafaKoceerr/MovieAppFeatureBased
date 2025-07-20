package com.mustafakocer.feature_auth.presentation.welcome.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_auth.presentation.welcome.viewmodel.WelcomeViewModel
import androidx.compose.runtime.getValue
import com.mustafakocer.feature_auth.presentation.welcome.contract.WelcomeEffect
import com.mustafakocer.navigation_contracts.actions.FeatureAuthNavActions


@Composable
fun WelcomeRoute(
    navActions: FeatureAuthNavActions,
    viewModel: WelcomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Effect'leri dinle.
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is WelcomeEffect.NavigateToHome -> {
                    navActions.navigateToHome()
                }
            }
        }
    }

    WelcomeScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )

}