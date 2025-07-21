package com.mustafakocer.feature_auth.welcome.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mustafakocer.core_preferences.datastore.PreferenceKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Önemli Not: SessionManager'ı neden core-preferences'da tanımlamadık da burada tanımladık?
 *
 * Çünkü auth, login işlemleri sadece bu feature'u ilgilendiriyor.
 * Session id'yi okuma işi feature-movies'i veya birden fazla feature'u da ilgilendirebilir, bunun için de core-domain'e bunu okuyacak bir interface tanımlayıp,
 * o interface'yi implement eden bir class'ı feature-auth katmanından hilt yardımıyla vereceğiz.
 * Böylece diğer feature'lar feature-auth'u implement etmeden, session_id'yi okuyabilecekler.
 */
@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    // Cihazda kayıtlı bir sesssion_id olup olmadığını dinleyen bir akış.
    val sessionIdFlow: Flow<String?> = dataStore.data.map { preferences ->
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