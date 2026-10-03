package com.mustafakocer.feature_movies.settings.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_domain.exception.toAppException
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.core_preferences.repository.ThemeRepository
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEffect
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEvent
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Manages the UI state and business logic for the settings screen.
 *
 * This ViewModel is responsible for:
 * - Reactively observing the current theme and language preferences from their respective repositories.
 * - Handling user events to change these preferences.
 * - Managing loading and error states during preference-saving operations.
 * - Emitting side effects for navigation or for applying critical configuration changes (like restarting the app).
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val languageRepository: LanguageRepository,
) : BaseViewModel<SettingsUiState, SettingsEvent, SettingsEffect>(
    SettingsUiState()
) {

    init {
        // Start observing the preference flows as soon as the ViewModel is created.
        observeThemeChanges()
        observeLanguageChanges()
    }

    /**
     * Handles incoming user events from the UI.
     */
    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ThemeSelected -> {
                // Prevent redundant operations if the user selects the currently active theme.
                if (event.theme == currentState.currentTheme) return
                saveSelectedTheme(event.theme)
            }

            is SettingsEvent.LanguageSelected -> {
                // Prevent redundant operations if the user selects the currently active language.
                if (event.language == currentState.currentLanguage) return
                saveSelectedLanguage(event.language)
            }

            is SettingsEvent.BackClicked -> {
                sendEffect(SettingsEffect.NavigateBack)
            }

            is SettingsEvent.DismissError -> {
                // Architectural Decision: This event is part of a "one-shot" error display pattern.
                // The UI shows the error from the state, then immediately sends this event to clear it.
                // This prevents the error from being shown again on recomposition or configuration change.
                setState { copy(error = null) }
            }
        }
    }

    /**
     * Subscribes to the theme preference flow from the repository.
     * This ensures the UI state is always in sync with the persisted theme preference.
     */
    private fun observeThemeChanges() {
        themeRepository.themeFlow
            .catch { e ->
                // If an error occurs while observing the flow, reflect it in the state.
                setState { copy(error = e.toAppException()) }
            }
            .onEach { theme ->
                // Update the state whenever a new theme preference is emitted.
                setState { copy(currentTheme = theme) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Subscribes to the language preference flow from the repository.
     * This ensures the UI state is always in sync with the persisted language preference.
     */
    private fun observeLanguageChanges() {
        languageRepository.languageFlow
            .catch { e ->
                setState { copy(error = e.toAppException()) }
            }
            .onEach { language ->
                setState { copy(currentLanguage = language) }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Saves the user's selected theme preference to the repository.
     * @param theme The [ThemePreference] to be saved.
     */
    private fun saveSelectedTheme(theme: ThemePreference) {
        if (currentState.isLoading) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                themeRepository.setTheme(theme)
                // On success, simply clear the loading state. The `observeThemeChanges` flow
                // will automatically receive the new value and update the UI state.
                setState { copy(isLoading = false) }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.toAppException()) }
            }
        }
    }

    /**
     * Saves the user's selected language preference to the repository.
     * @param language The [LanguagePreference] to be saved.
     */
    private fun saveSelectedLanguage(language: LanguagePreference) {
        if (currentState.isLoading) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                languageRepository.setLanguage(language)
                setState { copy(isLoading = false) }
                // Architectural Decision: Changing the app's language often requires an activity
                // restart to ensure all resources and configurations are reloaded correctly.
                // We send a specific side effect to the UI layer to trigger this "hoisted action,"
                // keeping the ViewModel decoupled from Android framework specifics.
                sendEffect(SettingsEffect.RestartActivity)
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.toAppException()) }
            }
        }
    }
}