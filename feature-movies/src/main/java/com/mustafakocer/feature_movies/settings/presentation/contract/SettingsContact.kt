package com.mustafakocer.feature_movies.settings.presentation.contract

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseUiEffect
import com.mustafakocer.core_common.presentation.BaseUiEvent
import com.mustafakocer.core_common.presentation.BaseUiState
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_preferences.models.LanguagePreference

// ==================== STATE ====================
data class SettingsUiState(
    val currentTheme: ThemePreference = ThemePreference.SYSTEM, // Varsayılan olarak Sistem Teması
    val currentLanguage: LanguagePreference = LanguagePreference.ENGLISH, // Varsayılan olarak Sistem Teması

    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false, // Bu ekranda kullanılmıyor ama tutarlılık için kalabilir.
    override val error: AppException? = null,
) : BaseUiState

// ==================== EVENT ====================
sealed interface SettingsEvent : BaseUiEvent {
    data class ThemeSelected(val theme: ThemePreference) : SettingsEvent
    data class LanguageSelected(val language: LanguagePreference) : SettingsEvent
    object BackClicked : SettingsEvent
    object DismissError : SettingsEvent
}

// ==================== EFFECT ====================
sealed interface SettingsEffect : BaseUiEffect {
    object NavigateBack : SettingsEffect
    object RestartActivity : SettingsEffect
}