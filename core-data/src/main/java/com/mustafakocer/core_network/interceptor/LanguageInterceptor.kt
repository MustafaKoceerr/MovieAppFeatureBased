package com.mustafakocer.core_network.interceptor

import com.mustafakocer.core_preferences.provider.LanguageProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * An OkHttp Interceptor that automatically appends the user's selected language
 * as a query parameter to every outgoing API request.
 *
 * @param languageProvider The provider that supplies the current language code.
 *
 * Architectural Note:
 * This centralizes the logic for request localization. By handling it at the network client
 * level, we ensure all API calls are localized consistently, and repositories remain unaware
 * of the language selection logic, promoting better separation of concerns.
 */
@Singleton
class LanguageInterceptor @Inject constructor(
    private val languageProvider: LanguageProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // OkHttp runs interceptors on its own background threads (Retrofit suspend calls are enqueued),
        // never on the main thread, so waiting for the stored language here is safe.
        val languageParam = runBlocking { languageProvider.getLanguageParam() }

        val originalRequest = chain.request()
        val url = originalRequest.url.newBuilder()
            .addQueryParameter("language", languageParam)
            .build()

        val newRequest = originalRequest.newBuilder().url(url).build()
        return chain.proceed(newRequest)
    }
}