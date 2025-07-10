package com.mustafakocer.core_preferences.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    /**
     * Theme preference key
     * Stores ThemePreference enum name as string
     */

    val THEME_PREFERENCE = stringPreferencesKey("theme_preference")

    // Future keys will be added when needed:
    // val LANGUAGE_PREFERENCE = stringPreferencesKey("language_preference")
    // val AUTH_TOKEN = stringPreferencesKey("auth_token")
}