package com.mustafakocer.feature_movies.settings.contract

import com.mustafakocer.core_common.presentation.BaseUiEvent
import com.mustafakocer.core_preferences.models.ThemePreference

sealed interface SettingsEvent : BaseUiEvent {

    data class ThemeSelected(val theme: ThemePreference) : SettingsEvent

    /**
     * User tapped back button
     */
    object BackPressed : SettingsEvent

    /**
     * User dismissed error message
     */
    object ErrorDismissed : SettingsEvent
}