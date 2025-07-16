package com.mustafakocer.feature_movies.settings.presentation.contract

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseUiEffect
import com.mustafakocer.core_common.presentation.BaseUiEvent
import com.mustafakocer.core_common.presentation.BaseUiState
import com.mustafakocer.core_preferences.models.ThemePreference

// ==================== STATE ====================
data class SettingsUiState(
    // DataStore'dan gelen ve o an seçili olan tema.
    val currentTheme: ThemePreference = ThemePreference.SYSTEM, // Varsayılan olarak Sistem Teması

    // isLoading, tema değiştirme işlemi sırasında kullanılacak.
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false, // Bu ekranda kullanılmıyor ama tutarlılık için kalabilir.
    override val error: AppException? = null
) : BaseUiState

// ==================== EVENT ====================
sealed interface SettingsEvent : BaseUiEvent {
    // Kullanıcı yeni bir tema seçtiğinde tetiklenir.
    data class ThemeSelected(val theme: ThemePreference) : SettingsEvent

    // Geri butonuna basıldığında tetiklenir.
    object BackClicked : SettingsEvent
}

// ==================== EFFECT ====================
sealed interface SettingsEffect : BaseUiEffect {
    object NavigateBack : SettingsEffect
    // Başarı veya hata durumları için tek bir Snackbar effect'i yeterli.
    data class ShowSnackbar(val message: String) : SettingsEffect
}