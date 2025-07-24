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
import com.mustafakocer.navigation_contracts.actions.movies.SettingsNavActions
import kotlinx.coroutines.flow.collectLatest

/**
 * A "Route" composable that acts as the smart container for the [SettingsScreen].
 *
 * Its primary responsibilities are:
 * 1.  Connecting the UI to the [SettingsViewModel].
 * 2.  Collecting and observing the UI state in a lifecycle-aware manner.
 * 3.  Handling one-time side effects, especially those that require "hoisted" actions like
 *     restarting the activity.
 *
 * Architectural Decision: This composable handles "hoisted actions". These are rare but critical
 * actions that a specific feature cannot perform on its own and must delegate to a higher-level
 * component, such as the main `Activity`. Examples include:
 * -   Restarting the activity (`activity.recreate()`) for language/theme changes.
 * -   Requesting system permissions.
 * -   Opening a system settings screen.
 * By passing a lambda (`onLanguageChanged`) for these actions, we keep the feature's ViewModel
 * and UI decoupled from the Android framework's `Activity` context, maintaining a clean architecture.
 *
 * @param navActions Provides standard navigation callbacks (e.g., `navigateUp`).
 * @param onLanguageChanged A hoisted callback to be invoked when the application needs to be restarted
 *                          to apply a language change.
 * @param viewModel The Hilt-injected [SettingsViewModel] instance for this screen.
 */
@Composable
fun SettingsRoute(
    navActions: SettingsNavActions,
    onLanguageChanged: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is SettingsEffect.NavigateBack -> navActions.navigateUp()
                // When the ViewModel requests a restart, we invoke the hoisted callback.
                is SettingsEffect.RestartActivity -> onLanguageChanged()
            }
        }
    }

    SettingsScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}