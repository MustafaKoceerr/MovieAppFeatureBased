package com.mustafakocer.movieappfeaturebasedclean.feature.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.exception.toAppException
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.core_preferences.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val currentTheme: ThemePreference = ThemePreference.SYSTEM,
    val currentLanguage: LanguagePreference = LanguagePreference.ENGLISH,
    val isSaving: Boolean = false,
    val error: AppException? = null,
    /** Set after a language change; the route recreates the activity and calls [SettingsViewModel.onRestartHandled]. */
    val restartRequired: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val languageRepository: LanguageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        themeRepository.themeFlow
            .catch { e -> _uiState.update { it.copy(error = e.toAppException()) } }
            .onEach { theme -> _uiState.update { it.copy(currentTheme = theme) } }
            .launchIn(viewModelScope)

        languageRepository.languageFlow
            .catch { e -> _uiState.update { it.copy(error = e.toAppException()) } }
            .onEach { language -> _uiState.update { it.copy(currentLanguage = language) } }
            .launchIn(viewModelScope)
    }

    fun onThemeSelected(theme: ThemePreference) {
        val state = _uiState.value
        if (theme == state.currentTheme || state.isSaving) return
        save { themeRepository.setTheme(theme) }
    }

    fun onLanguageSelected(language: LanguagePreference) {
        val state = _uiState.value
        if (language == state.currentLanguage || state.isSaving) return
        save(onSaved = { _uiState.update { it.copy(restartRequired = true) } }) {
            languageRepository.setLanguage(language)
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(error = null) }
    }

    fun onRestartHandled() {
        _uiState.update { it.copy(restartRequired = false) }
    }

    private fun save(onSaved: () -> Unit = {}, block: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                block()
                _uiState.update { it.copy(isSaving = false) }
                onSaved()
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.toAppException()) }
            }
        }
    }
}
