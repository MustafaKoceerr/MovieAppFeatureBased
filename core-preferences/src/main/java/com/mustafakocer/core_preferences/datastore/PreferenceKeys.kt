package com.mustafakocer.core_preferences.datastore

import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Defines a centralized contract for all keys used with Jetpack DataStore.
 *
 * Architectural Note:
 * This object centralizes all preference keys, preventing the use of "magic strings" and
 * ensuring type safety across the application when interacting with DataStore.
 *
 * This `core_preferences` module's responsibility is only to define the *keys* (the contract),
 * not to provide the actual DataStore instance. The instance itself should be provided via
 * dependency injection from a higher-level module (like the `:app` module), adhering to the
 * Dependency Inversion Principle.
 */
object PreferenceKeys {

    val THEME_PREFERENCE = stringPreferencesKey("theme_preference")

    val LANGUAGE_PREFERENCE = stringPreferencesKey("language_preference")

    /**
     * Stores the active user session ID obtained after a successful login.
     */
    val SESSION_ID = stringPreferencesKey("session_id")
}