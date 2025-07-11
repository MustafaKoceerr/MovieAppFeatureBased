package com.mustafakocer.core_preferences.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * DI package'ı için yorum:
 * Aksiyon: Bu dosyayı tamamen sil. core modülünün görevi DataStore'u sağlamak değil, sadece PreferenceKeys gibi tanımları barındırmaktır.
 *
 */
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