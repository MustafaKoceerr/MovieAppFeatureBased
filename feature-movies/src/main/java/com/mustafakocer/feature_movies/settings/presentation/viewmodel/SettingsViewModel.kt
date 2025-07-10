package com.mustafakocer.feature_movies.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_common.presentation.UiContract
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.data_common.preferences.repository.ThemeRepository
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEffect
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEvent
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
) : ViewModel(), UiContract<SettingsUiState, SettingsEvent, SettingsEffect> {
    // MVI implementation...
    private val _uiState = MutableStateFlow(SettingsUiState())
    override val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SettingsEffect>()
    override val uiEffect: SharedFlow<SettingsEffect> = _uiEffect.asSharedFlow()

    init {
        observeThemeChanges()
    }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ThemeSelected -> handleThemeSelection(event.theme)
            is SettingsEvent.BackPressed -> handleBackPressed()
            is SettingsEvent.ErrorDismissed -> handleErrorDismissed()
        }
    }

    // ==================== PRIVATE EVENT HANDLERS ====================

    private fun handleThemeSelection(theme: ThemePreference) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                themeRepository.setTheme(theme)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentTheme = theme
                )

                sendEffect(SettingsEffect.ShowSuccessSnackbar("Theme changed to ${theme.displayName}"))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save theme"
                )

                sendEffect(SettingsEffect.ShowErrorSnackbar("Failed to change theme"))
            }
        }
    }

    private fun handleBackPressed() {
        viewModelScope.launch {
            sendEffect(SettingsEffect.NavigateBack)
        }
    }

    private fun handleErrorDismissed() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // ==================== PRIVATE HELPERS ====================

    private fun observeThemeChanges() {
        viewModelScope.launch {
            themeRepository.themeFlow.collect { theme ->
                _uiState.value = _uiState.value.copy(currentTheme = theme)
            }
        }
    }

    private suspend fun sendEffect(effect: SettingsEffect) {
        _uiEffect.emit(effect)
    }
}