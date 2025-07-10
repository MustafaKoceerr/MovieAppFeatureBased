package com.mustafakocer.feature_movies.settings.presentation.contract

import com.mustafakocer.core_common.presentation.BaseUiState
import com.mustafakocer.core_preferences.models.ThemePreference

data class SettingsUiState(
    override val isLoading: Boolean = false,
    override val error: String? = null,
    val currentTheme: ThemePreference = ThemePreference.DEFAULT
) : BaseUiState