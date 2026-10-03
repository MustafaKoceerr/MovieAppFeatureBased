package com.mustafakocer.core_preferences.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mustafakocer.core_preferences.datastore.PreferenceKeys
import com.mustafakocer.core_preferences.models.ThemePreference
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Manages the persistence and retrieval of the user's selected theme preference.
 *
 * @param dataStore The DataStore instance used for persistence.
 *
 * Architectural Note:
 * This repository centralizes the logic for a shared, application-wide user preference.
 * It abstracts the DataStore implementation from the rest of the app, providing a clean,
 * reactive API (`themeFlow`) for observing changes and a simple suspend function for
 * updating the value. This decouples feature modules from the data persistence details.
 */
@Singleton
class ThemeRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    /**
     * A reactive stream of the current [ThemePreference].
     */
    val themeFlow: Flow<ThemePreference> = dataStore.data
        .map { preferences ->
            val themeString = preferences[PreferenceKeys.THEME_PREFERENCE]
            ThemePreference.fromString(themeString)
        }.catch {
            // Why this catch block is important:
            // If any error occurs during DataStore read/map operations (e.g., disk I/O issue),
            // this prevents the flow from crashing and gracefully falls back to the default theme.
            emit(ThemePreference.DEFAULT)
        }

    suspend fun setTheme(theme: ThemePreference) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.THEME_PREFERENCE] = theme.name
            }
        } catch (e: Exception) {
            // This operation is designed to be fire-and-forget from the UI's perspective.
            // In a production environment, this failure would be logged to an analytics service.
        }
    }
}