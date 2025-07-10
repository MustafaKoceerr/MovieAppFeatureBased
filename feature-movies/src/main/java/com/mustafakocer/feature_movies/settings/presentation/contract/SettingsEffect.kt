package com.mustafakocer.feature_movies.settings.contract

import com.mustafakocer.core_common.presentation.BaseUiEffect

sealed interface SettingsEffect : BaseUiEffect {
    object NavigateBack : SettingsEffect
    data class ShowSuccessSnackbar(val message: String) : SettingsEffect
    data class ShowErrorSnackbar(val message: String) : SettingsEffect
}