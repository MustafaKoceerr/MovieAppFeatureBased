package com.mustafakocer.core_network.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mustafakocer.core_domain.config.NetworkConfigProvider
import com.mustafakocer.core_network.config.NetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides a configured Kotlinx Serialization Json instance for parsing API responses.
     */
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    /**
     * Provides an HttpLoggingInterceptor that logs network traffic only in debug builds.
     */
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(
        configProvider: NetworkConfigProvider,
    ): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (configProvider.isDebug && configProvider.enableLogging) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    /**
     * Provides a shared OkHttp Cache to reduce redundant network calls.
     */
    @Provides
    @Singleton
    fun provideOkHttpCache(@ApplicationContext context: Context): Cache {
        return Cache(context.cacheDir, NetworkConfig.CACHE_SIZE_BYTES)
    }

    /**
     * Provides the main OkHttpClient instance, configured with timeouts, caching, and interceptors.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        apiKeyInterceptor: ApiKeyInterceptor,
        languageInterceptor: LanguageInterceptor,
        cache: Cache,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(languageInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Provides the main Retrofit instance, configured for the application's API.
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        configProvider: NetworkConfigProvider,
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(configProvider.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}