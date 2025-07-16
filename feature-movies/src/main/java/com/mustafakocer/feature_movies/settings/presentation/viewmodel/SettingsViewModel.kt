package com.mustafakocer.feature_movies.settings.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseViewModel
import com.mustafakocer.core_common.presentation.LoadingType
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.data_common.preferences.repository.ThemeRepository
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEffect
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEvent
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
) : BaseViewModel<SettingsUiState, SettingsEvent, SettingsEffect>(SettingsUiState()) {

    init {
        // ViewModel oluşturulur oluşturulmaz, DataStore'daki tema değişikliklerini
        // sürekli olarak dinlemeye başlıyoruz.
        observeThemeChanges()
    }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ThemeSelected -> {
                saveSelectedTheme(event.theme)
            }

            is SettingsEvent.BackClicked -> {
                sendEffect(SettingsEffect.NavigateBack)
            }
        }
    }

    /**
     * DataStore'dan gelen tema akışını dinler ve UI state'ini günceller.
     */
    private fun observeThemeChanges() {
        viewModelScope.launch {
            themeRepository.themeFlow
                .catch { exception ->
                    // Tema okunurken bir hata olursa yakala.
                    setState { handleError(AppException.DataException.ParseError(originalCause = exception)) }
                }
                .collect { theme ->
                    // Her yeni tema geldiğinde, state'i güncelle.
                    setState { copy(currentTheme = theme) }
                }
        }
    }

    /**
     * Kullanıcının seçtiği temayı DataStore'a kaydeder.
     * Bu tek seferlik bir işlem olduğu için executeSafeOnce kullanıyoruz.
     */
    private fun saveSelectedTheme(theme: ThemePreference) {
        // Zaten bir işlem devam ediyorsa, yenisini başlatma.
        if (currentState.isLoading) return

        executeSafeOnce(loadingType = LoadingType.MAIN) {
            themeRepository.setTheme(theme)
            // Başarılı olduğunda kullanıcıya geri bildirim ver.
            sendEffect(SettingsEffect.ShowSnackbar("Tema değiştirildi: ${theme.name}"))
        }
    }

    // BaseViewModel'in zorunlu kıldığı abstract metodları implemente ediyoruz.
    override fun handleError(error: AppException): SettingsUiState {
        // Hata durumunda kullanıcıya geri bildirim ver.
        sendEffect(SettingsEffect.ShowSnackbar(error.userMessage))
        return currentState.copy(error = error)
    }

    override fun setLoading(loadingType: LoadingType, isLoading: Boolean): SettingsUiState {
        // Bu ekranda sadece ana yükleme durumu var (isRefreshing yok).
        return currentState.copy(isLoading = isLoading)
    }
}