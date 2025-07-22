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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val languageRepository: LanguageRepository,
) : BaseViewModel<SettingsUiState, SettingsEvent, SettingsEffect>(
    SettingsUiState()
) {

    init {
        observeThemeChanges()
        observeLanguageChanges()
    }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ThemeSelected -> {
                // Seçilen tema zaten mevcut temaysa, hiçbir şey yapma.
                if (event.theme == currentState.currentTheme) return
                saveSelectedTheme(event.theme)
            }

            is SettingsEvent.LanguageSelected -> {
                // Seçilen dil zaten mevcut dilse, hiçbir şey yapma.
                if (event.language == currentState.currentLanguage) return
                saveSelectedLanguage(event.language)
            }

            is SettingsEvent.BackClicked -> {
                sendEffect(SettingsEffect.NavigateBack)
            }
            // Hatanın Snackbar'da gösterildikten sonra state'den temizlenmesi için.
            is SettingsEvent.DismissError -> {
                setState { copy(error = null) }
            }
        }
    }

    private fun observeThemeChanges() {
        themeRepository.themeFlow
            .catch { e ->
                // Flow'u dinlerken bir hata oluşursa, state'e yansıt.
                setState { copy(error = e.toAppException()) }
            }
            .onEach { theme ->
                // Her yeni tema geldiğinde, state'i güncelle.
                setState { copy(currentTheme = theme) }
            }
            .launchIn(viewModelScope) // Flow'u viewModelScope'ta başlat ve dinle.
    }

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

    private fun saveSelectedTheme(theme: ThemePreference) {
        // Zaten bir işlem devam ediyorsa yenisini başlatma
        if (currentState.isLoading) return

        viewModelScope.launch {
            // Yükleme durumunu başlat.
            setState { copy(isLoading = true) }
            try {
                themeRepository.setTheme(theme)
                // Başarılı olduğunda yükleme durumunu bitir.
                setState { copy(isLoading = false) }
            } catch (e: Exception) {
                // Hata durumunda, hatayı state'e yaz ve yükleme durumunu bitir.
                setState { copy(isLoading = false, error = e.toAppException()) }
            }
        }
    }


    private fun saveSelectedLanguage(language: LanguagePreference) {
        if (currentState.isLoading) return

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            try {
                languageRepository.setLanguage(language)
                // Başarılı olduğunda, yükleme durumunu bitir ve Activity'yi yeniden başlatma effect'ini gönder.
                setState { copy(isLoading = false) }
                sendEffect(SettingsEffect.RestartActivity)
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.toAppException()) }
            }
        }
    }

}