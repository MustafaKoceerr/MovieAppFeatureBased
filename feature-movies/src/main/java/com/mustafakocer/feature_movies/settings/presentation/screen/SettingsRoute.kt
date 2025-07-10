package com.mustafakocer.feature_movies.settings.presentation.screen


import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustafakocer.feature_movies.settings.presentation.viewmodel.SettingsViewModel
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEffect
import com.mustafakocer.navigation_contracts.SettingsNavActions

/**
 * Settings Route - Effect handling and navigation
 *
 * RESPONSIBILITY: Handle navigation effects and side effects
 * PATTERN: Route handles effects, Screen handles pure UI
 */
@Composable
fun SettingsRoute(
    navActions: SettingsNavActions,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle UI Effects
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is SettingsEffect.NavigateBack -> {
                    navActions.navigateBack()
                }

                is SettingsEffect.ShowSuccessSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }

                is SettingsEffect.ShowErrorSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    // Render the screen
    SettingsScreen(
        contract = viewModel,
        snackbarHostState = snackbarHostState
    )
}