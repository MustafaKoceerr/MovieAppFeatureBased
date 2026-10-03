package com.mustafakocer.feature_movies.settings.presentation.contract

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.presentation.BaseUiEffect
import com.mustafakocer.core_domain.presentation.BaseUiEvent
import com.mustafakocer.core_domain.presentation.BaseUiState
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.models.ThemePreference

// --- STATE ---

/**
 * Represents the complete UI state for the settings screen.
 *
 * @property currentTheme The currently selected theme preference (e.g., Light, Dark, System).
 * @property currentLanguage The currently selected language preference (e.g., English, Turkish).
 * @property isLoading A flag for any general loading operations on this screen.
 * @property isRefreshing A flag for user-initiated refresh actions.
 * @property error Holds any critical error that occurs on this screen.
 */
data class SettingsUiState(
    val currentTheme: ThemePreference = ThemePreference.SYSTEM,
    val currentLanguage: LanguagePreference = LanguagePreference.ENGLISH,
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false,
    override val error: AppException? = null,
) : BaseUiState

// --- EVENT ---

sealed interface SettingsEvent : BaseUiEvent {

    data class ThemeSelected(val theme: ThemePreference) : SettingsEvent

    data class LanguageSelected(val language: LanguagePreference) : SettingsEvent

    object BackClicked : SettingsEvent

    object DismissError : SettingsEvent
}

// --- EFFECT ---

sealed interface SettingsEffect : BaseUiEffect {

    object NavigateBack : SettingsEffect

    /**
     * Instructs the application to restart itself.
     *
     * Architectural Decision: Some configuration changes, particularly language updates on Android,
     * do not propagate correctly throughout the entire application without a full recreation of the
     * activities. This effect provides a clean, explicit signal from the ViewModel to the UI layer
     * (e.g., the main Activity) to trigger this restart process, ensuring the new settings are
     * applied globally and consistently.
     */
    object RestartActivity : SettingsEffect
}