package com.mustafakocer.core_preferences.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mustafakocer.core_preferences.datastore.PreferenceKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Oturum kimliğini (Session ID) DataStore üzerinde yönetir.
 * Bu sınıf, oturum oluşturma ve silme işlemlerinin merkezi noktasıdır.
 * @Singleton olarak işaretlenmiştir çünkü uygulama boyunca tek bir örneği olmalıdır.
 */
@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    /**
     * Oturum kimliğini içeren bir flow döndürür.
     * Oturum kapalıysa veya hiç oluşturulmamışsa null değer yayar.
     * Bu Flow, oturum durumundaki değişiklikleri reaktif olarak dinlemek için kullanılır.
     */
    val sessionIdFlow: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.SESSION_ID]
        }

    /**
     * Verilen oturum kimliğini datastore'a kaydeder.
     */
    suspend fun saveSessionId(sessionId: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.SESSION_ID] = sessionId
        }
    }

    /**
     * Kayıtlı oturum kimliğini DataStore'dan siler.
     */
    suspend fun clearSessionId() {
        dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.SESSION_ID)
        }
    }
}