package com.mustafakocer.core_preferences.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mustafakocer.core_preferences.datastore.PreferenceKeys
import com.mustafakocer.core_preferences.models.LanguagePreference
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * A repository responsible for managing the application's language preference.
 *
 * @param dataStore The Jetpack DataStore instance for persistence.
 *
 * Architectural Note:
 * This repository abstracts the underlying DataStore operations for language settings. It provides
 * a clean, type-safe API to the rest of the application, exposing a reactive [Flow] of the
 * current [LanguagePreference] and a simple suspend function to update it. This decouples
 * business logic (e.g., in ViewModels) from the specific implementation details of data persistence.
 */
@Singleton
class LanguageRepository(
    private val dataStore: DataStore<Preferences>,
) {

    /**
     * A reactive stream that emits the current [LanguagePreference] whenever it changes in DataStore.
     *
     * Why `.catch`: This operator provides resilience. If an error occurs while reading from
     * DataStore (e.g., disk corruption), it prevents the flow from crashing and gracefully
     * emits the default language preference instead.
     */
    val languageFlow: Flow<LanguagePreference> = dataStore.data
        .map { preferences ->
            val languageString = preferences[PreferenceKeys.LANGUAGE_PREFERENCE]
            LanguagePreference.fromString(languageString)
        }.catch {
            // In case of any exception during the map operation, emit the default value.
            emit(LanguagePreference.DEFAULT)
        }

    suspend fun setLanguage(language: LanguagePreference) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LANGUAGE_PREFERENCE] = language.name
        }
    }
}