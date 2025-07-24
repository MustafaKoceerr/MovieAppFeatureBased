package com.mustafakocer.core_network.di

import com.mustafakocer.core_domain.config.NetworkConfigProvider
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

/**
 * An OkHttp Interceptor that automatically injects the required API key into every outgoing request.
 *
 * @param configProvider The provider for network configuration values like the API key.
 *
 * Architectural Note:
 * Using an interceptor is a clean and robust way to handle cross-cutting concerns like
 * authentication. It automatically adds the API key to all network calls at the HTTP client
 * level, which means individual repository or service methods don't need to be aware of this
 * implementation detail. This greatly simplifies the data layer and prevents accidental
 * omission of the key.
 */
@Singleton
class ApiKeyInterceptor @Inject constructor(
    private val configProvider: NetworkConfigProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalHttpUrl = originalRequest.url

        val newUrl = originalHttpUrl.newBuilder()
            .addQueryParameter("api_key", configProvider.apiKey)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}