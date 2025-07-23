package com.mustafakocer.core_preferences.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mustafakocer.core_preferences.datastore.PreferenceKeys
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manages the user's session ID, handling its persistence in DataStore.
 *
 * @param dataStore The Jetpack DataStore instance for persistence.
 *
 * Architectural Note:
 * This class serves as the central point of control for the user's session state. By
 * abstracting the DataStore operations, it provides a clean and dedicated API for login
 * (`saveSessionId`) and logout (`clearSessionId`) procedures. As a singleton, it ensures
 * consistent session management throughout the application's lifecycle.
 */
@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    /**
     * A reactive stream that emits the current session ID.
     * It emits the ID string if a session exists, or `null` if the user is logged out.
     * This flow is ideal for observing authentication state changes across the app.
     */
    val sessionIdFlow: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.SESSION_ID]
        }

    suspend fun saveSessionId(sessionId: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.SESSION_ID] = sessionId
        }
    }

    suspend fun clearSessionId() {
        dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.SESSION_ID)
        }
    }
}