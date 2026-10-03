package com.mustafakocer.core_preferences.provider

import com.mustafakocer.core_preferences.repository.LanguageRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

/**
 * Provides the API language parameter (e.g. `en-US`) for the user's selected language.
 *
 * It always reads the stored preference instead of keeping its own cached copy. A separate cache
 * updated by a background collector can be empty before the first value arrives and can be stale
 * right after the language changes, so a request could be sent (and cached) with the wrong
 * language. DataStore keeps the preferences in memory, so reading them is cheap.
 *
 * @param languageRepository The repository for accessing persisted language preferences.
 */
@Singleton
class LanguageProvider @Inject constructor(
    private val languageRepository: LanguageRepository,
) {

    /** The API language of the currently stored preference (the default language if none is stored). */
    suspend fun getLanguageParam(): String = languageRepository.languageFlow.first().apiParam
}
