package com.mustafakocer.core_preferences.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mustafakocer.core_preferences.datastore.PreferenceKeys
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton
import com.mustafakocer.core_preferences.models.LanguagePreference
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Singleton
class LanguageRepository(
    private val dataStore: DataStore<Preferences>,
) {

    val languageFlow: Flow<LanguagePreference> = dataStore.data
        .map { preferences ->
            val languageString = preferences[PreferenceKeys.LANGUAGE_PREFERENCE]
            LanguagePreference.fromString(languageString)
        }.catch {
            emit(LanguagePreference.DEFAULT)
        }

    suspend fun setLanguage(language: LanguagePreference) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LANGUAGE_PREFERENCE] = language.name
        }
    }
}