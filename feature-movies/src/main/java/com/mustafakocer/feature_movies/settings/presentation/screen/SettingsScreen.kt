package com.mustafakocer.feature_movies.settings.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_ui.component.error.toErrorInfo
import com.mustafakocer.feature_movies.settings.presentation.component.LanguageSelectionSection
import com.mustafakocer.feature_movies.settings.presentation.component.SettingsHeader
import com.mustafakocer.feature_movies.settings.presentation.component.SettingsTopBar
import com.mustafakocer.feature_movies.settings.presentation.component.ThemeSelectionSection
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEvent
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsUiState

/**
 * The main stateless UI component for the settings screen.
 *
 * This composable is responsible for laying out the screen's visual elements based on the
 * provided [state] and forwarding user interactions to the ViewModel via the [onEvent] callback.
 * It also encapsulates the logic for displaying error Snackbars.
 *
 * @param state The current [SettingsUiState] to render.
 * @param onEvent A function to call when a user interaction occurs.
 * @param snackbarHostState The state manager for displaying Snackbars.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    // Architectural Decision: Error handling is managed reactively within this composable.
    // When the `state.error` is not null, a `LaunchedEffect` is triggered. This effect shows a
    // Snackbar and then immediately dispatches a `DismissError` event. This pattern ensures that
    // the error is shown only once per error event and prevents the Snackbar from reappearing
    // on every recomposition.
    state.error?.let { error ->
        val errorInfo = error.toErrorInfo()
        val message = "${errorInfo.title}: ${errorInfo.description}"

        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            // After showing the error, we notify the ViewModel to clear it from the state.
            onEvent(SettingsEvent.DismissError)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SettingsTopBar(
                onBackPressed = { onEvent(SettingsEvent.BackClicked) }
            )
        }
    ) { paddingValues ->
        // The main scrollable content of the screen is delegated to a private composable.
        SettingsContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = state,
            onThemeSelected = { theme ->
                onEvent(SettingsEvent.ThemeSelected(theme))
            },
            onLanguageSelected = { language ->
                onEvent(SettingsEvent.LanguageSelected(language))
            }
        )
    }
}

/**
 * Lays out the scrollable main content of the settings screen.
 *
 * @param modifier The modifier to be applied to the root Column.
 * @param state The current UI state.
 * @param onThemeSelected Callback for when a new theme is selected.
 * @param onLanguageSelected Callback for when a new language is selected.
 */
@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    state: SettingsUiState,
    onThemeSelected: (ThemePreference) -> Unit,
    onLanguageSelected: (LanguagePreference) -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()) // Makes the content scrollable.
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp) // Adds space between sections.
    ) {
        SettingsHeader()

        ThemeSelectionSection(
            currentTheme = state.currentTheme,
            // The `isLoading` state can be used to disable the selection buttons or show an
            // indicator while a preference is being saved, preventing concurrent modifications.
            isLoading = state.isLoading,
            onThemeSelected = onThemeSelected
        )

        LanguageSelectionSection(
            currentLanguage = state.currentLanguage,
            isLoading = state.isLoading,
            onLanguageSelected = onLanguageSelected
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}