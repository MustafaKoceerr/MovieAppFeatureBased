package com.mustafakocer.feature_auth.account.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_auth.account.presentation.contract.AccountEffect
import com.mustafakocer.feature_auth.account.presentation.viewmodel.AccountViewModel
import com.mustafakocer.navigation_contracts.actions.auth.AccountNavActions

/**
 * The main entry point and controller for the Account screen feature.
 *
 * @param navActions An implementation of [AccountNavActions] for triggering navigation.
 * @param viewModel The ViewModel responsible for the Account screen's logic.
 *
 * Architectural Note:
 * This Composable acts as the "route" or controller for the feature. It connects the
 * ViewModel's state and effects to the UI and navigation layers.
 * - It observes `uiState` using `collectAsStateWithLifecycle` for lifecycle-aware state updates.
 * - It observes `uiEffect` to handle one-time navigation events via the `AccountNavActions` contract.
 * - It passes the current state and the `onEvent` handler down to the "dumb" `AccountScreen` Composable.
 */
@Composable
fun AccountRoute(
    navActions: AccountNavActions,
    viewModel: AccountViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AccountEffect.NavigateToWelcome -> navActions.navigateToWelcome()
                is AccountEffect.NavigateBack -> navActions.navigateUp()
            }
        }
    }

    AccountScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}