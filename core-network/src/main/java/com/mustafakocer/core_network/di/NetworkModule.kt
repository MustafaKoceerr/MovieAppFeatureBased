package com.mustafakocer.core_network.di

import com.mustafakocer.core_common.config.NetworkConfigProvider
import com.mustafakocer.core_network.config.NetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// # Hilt module for DI
/**
 * UPDATED: Complete Network Module with all NetworkConfig settings
 *
 * ✅ Uses NetworkConfig constants
 * ✅ Full OkHttpClient configuration
 * ✅ Single Retrofit instance for entire app
 * ✅ App-specific configs injected via NetworkConfigProvider
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true    // API'dan bilinmeyen field'lar ignore
            coerceInputValues = true    // Type coercion (string->int vs.)
            isLenient = true           // Flexible parsing
        }
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(
        configProvider: NetworkConfigProvider
    ): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (configProvider.enableLogging) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

//    @Provides
//    @Singleton
//    fun provideCache(): Cache? {
//        return null // Will be provided by app module if needed
//    }

    @Provides
    @Singleton
    fun provideDispatcher(): Dispatcher {
        return Dispatcher().apply {
            maxRequests = NetworkConfig.MAX_REQUESTS_PER_HOST * 2 // Total requests
            maxRequestsPerHost = NetworkConfig.MAX_REQUESTS_PER_HOST
        }
    }

    @Provides
    @Singleton
    fun provideConnectionPool(): ConnectionPool {
        return ConnectionPool(
            maxIdleConnections = NetworkConfig.MAX_IDLE_CONNECTIONS,
            keepAliveDuration = NetworkConfig.KEEP_ALIVE_DURATION,
            timeUnit = TimeUnit.MINUTES
        )
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        configProvider: NetworkConfigProvider,
        loggingInterceptor: HttpLoggingInterceptor,
        cache: Cache?,
        dispatcher: Dispatcher,
        connectionPool: ConnectionPool,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // ==================== TIMEOUTS ====================
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)

            // ==================== PERFORMANCE CONFIGS ====================
            .dispatcher(dispatcher)
            .connectionPool(connectionPool)
            .cache(cache)

            // ==================== INTERCEPTORS ====================
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", configProvider.userAgent)
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)

            // ==================== CACHE POLICY ====================
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=${NetworkConfig.CACHE_MAX_AGE}")
                    .build()
            }

            // ==================== DEVELOPMENT SETTINGS ====================
            .apply {
                if (configProvider.isDebug) {
                    // Disable SSL verification in debug (for testing only)
                    // hostnameVerifier { _, _ -> true }
                }
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        configProvider: NetworkConfigProvider,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(configProvider.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
}