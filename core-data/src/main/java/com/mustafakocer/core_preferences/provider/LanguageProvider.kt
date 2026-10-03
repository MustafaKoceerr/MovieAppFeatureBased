package com.mustafakocer.core_preferences.provider

import com.mustafakocer.core_preferences.repository.LanguageRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * A singleton provider that maintains a high-performance, in-memory cache of the current language setting.
 *
 * @param languageRepository The repository for accessing persisted language preferences.
 *
 * Architectural Note:
 * This class is a critical optimization. It is designed to provide synchronous, non-blocking
 * access to the user's selected language. It achieves this by observing the language preference
 * from DataStore in a background coroutine and caching the result in a `volatile` variable.
 * This allows components on performance-critical paths, like an OkHttp Interceptor, to get the
 * current language instantly without performing any disk I/O.
 */
@Singleton
class LanguageProvider @Inject constructor(
    languageRepository: LanguageRepository,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Why `@Volatile`:
    // This keyword guarantees that writes to this variable from the background coroutine
    // are immediately made visible to all other threads (e.g., the network interceptor's thread).
    // This prevents reading a stale value.
    @Volatile
    private var currentLanguageParam: String = ""

    init {
        // The init block launches a long-lived coroutine to listen for language changes.
        // This "hot" collection ensures our in-memory cache is always up-to-date.
        scope.launch {
            languageRepository.languageFlow.collect { newLanguage ->
                currentLanguageParam = newLanguage.apiParam
            }
        }
    }

    fun getLanguageParam(): String {
        return currentLanguageParam
    }
}