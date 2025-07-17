package com.mustafakocer.core_network.di

import com.mustafakocer.core_common.config.NetworkConfigProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyInterceptor @Inject constructor(
    private val configProvider: NetworkConfigProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
// 1. Orijinal isteği al.
        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url
        // 2. URL'ye "api_key" query parametresini ekle.
        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("api_key", configProvider.apiKey)
            .build()

        // 3. Yeni URL ile isteği yeniden oluştur.
        val requestBuilder = originalRequest.newBuilder().url(url)
        val newRequest = requestBuilder.build()

        // 4. Yeni isteği zincire devam ettir.
        return chain.proceed(newRequest)
    }
}