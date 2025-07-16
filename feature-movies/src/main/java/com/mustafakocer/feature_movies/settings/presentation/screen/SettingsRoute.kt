package com.mustafakocer.feature_movies.settings.presentation.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEffect
import com.mustafakocer.feature_movies.settings.presentation.viewmodel.SettingsViewModel
import com.mustafakocer.navigation_contracts.actions.FeatureMoviesNavActions
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsRoute(
    navActions: FeatureMoviesNavActions,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Yan etkileri (Effect'leri) dinleyip yöneten bölüm.
    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is SettingsEffect.NavigateBack -> {
                    navActions.navigateUp()
                }
                is SettingsEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
            }
        }
    }

    // Saf UI bileşenini çağırıyoruz.
    SettingsScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}