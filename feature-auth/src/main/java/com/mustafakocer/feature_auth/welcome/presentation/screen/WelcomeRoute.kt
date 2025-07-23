package com.mustafakocer.feature_auth.welcome.presentation.screen

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_auth.welcome.presentation.contract.WelcomeEffect
import com.mustafakocer.feature_auth.welcome.presentation.viewmodel.WelcomeViewModel
import com.mustafakocer.navigation_contracts.actions.auth.WelcomeNavActions

/**
 * The main entry point and controller for the Welcome screen feature.
 *
 * @param navActions An implementation of [WelcomeNavActions] for triggering navigation.
 * @param viewModel The ViewModel responsible for the Welcome screen's logic.
 *
 * Architectural Note:
 * This Composable acts as the "route" or controller for the feature. It is responsible for
 * orchestrating interactions between the ViewModel and the outside world.
 * - It observes `uiState` to drive the UI.
 * - It uses a `LaunchedEffect` to handle one-time side effects (`uiEffect`), such as
 *   triggering navigation via the `navActions` contract or launching an external browser.
 * - Using `LocalContext` here to start an `Intent` is appropriate, as this is a UI-layer
 *   concern, keeping the ViewModel free of Android framework dependencies.
 */
@Composable
fun WelcomeRoute(
    navActions: WelcomeNavActions,
    viewModel: WelcomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is WelcomeEffect.NavigateToHome -> {
                    navActions.navigateToHome()
                }
                is WelcomeEffect.NavigateToTmdbLogin -> {
                    // Create an intent to open the device's default web browser for authentication.
                    val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                    context.startActivity(intent)
                }
            }
        }
    }

    WelcomeScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )
}