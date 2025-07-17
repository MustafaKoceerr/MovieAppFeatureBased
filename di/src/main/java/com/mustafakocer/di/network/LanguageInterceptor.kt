package com.mustafakocer.di.network

import com.mustafakocer.core_preferences.provider.LanguageProvider // <-- YENİ IMPORT
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageInterceptor @Inject constructor(
    private val languageProvider: LanguageProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Artık runBlocking yok! Sadece hafızadaki değeri anında oku.
        val languageParam = languageProvider.getLanguageParam()

        val originalRequest = chain.request()
        val url = originalRequest.url.newBuilder()
            .addQueryParameter("language", languageParam)
            .build()

        val newRequest = originalRequest.newBuilder().url(url).build()
        return chain.proceed(newRequest)
    }

}