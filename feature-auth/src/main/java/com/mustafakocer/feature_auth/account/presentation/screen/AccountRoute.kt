package com.mustafakocer.feature_auth.account.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_auth.account.presentation.contract.AccountEffect
import com.mustafakocer.feature_auth.account.presentation.viewmodel.AccountViewModel
import com.mustafakocer.navigation_contracts.actions.FeatureAuthNavActions

@Composable
fun AccountRoute(
    navActions: FeatureAuthNavActions,
    viewModel: AccountViewModel = hiltViewModel(),
) {
    // ViewModel'den gelen canlı state'i dinliyoruz.
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // ViewModel'den gelen tek seferlik Effect'leri dinliyoruz.
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is AccountEffect.NavigateToWelcome -> navActions.navigateToWelcome()
                is AccountEffect.NavigateBack -> navActions.navigateUp()
            }
        }
    }

    // AccountScreen'e canlı state'i ve event handler'ı paslıyoruz.
    AccountScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}