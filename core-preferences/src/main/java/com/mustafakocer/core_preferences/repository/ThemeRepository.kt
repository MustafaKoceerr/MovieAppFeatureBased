package com.mustafakocer.core_preferences.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mustafakocer.core_preferences.datastore.PreferenceKeys
import com.mustafakocer.core_preferences.models.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>, // ← From core-preferences
) {
    /**
     * Observe theme preference changes
     * Reactive stream for all features
     */
    val themeFlow: Flow<ThemePreference> = dataStore.data
        .map { preferences ->
            val themeString = preferences[PreferenceKeys.THEME_PREFERENCE]
            ThemePreference.fromString(themeString)
        }.catch { throwable ->
            // Graceful fallback to default theme
            emit(ThemePreference.DEFAULT)
        }

    /**
     * Set theme preference
     * Used by settings and other features
     */
    suspend fun setTheme(theme: ThemePreference) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferenceKeys.THEME_PREFERENCE] = theme.name
            }
        } catch (e: Exception) {
            // Log error in production
            // Could emit to analytics here
        }
    }

}