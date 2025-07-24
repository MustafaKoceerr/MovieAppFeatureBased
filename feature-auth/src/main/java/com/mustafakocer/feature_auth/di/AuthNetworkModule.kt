package com.mustafakocer.feature_auth.di

import com.mustafakocer.feature_auth.shared.data.api.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

/**
 * A Hilt module for providing network-related dependencies specific to the authentication feature.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthNetworkModule {

    /**
     * Provides a singleton instance of the [AuthApiService].
     *
     * Architectural Note:
     * This provider leverages the application-wide `Retrofit` instance (configured in the
     * `:core_network` module) to create a feature-specific API service. This is an efficient
     * pattern that reuses the shared `OkHttpClient` while defining only the endpoints relevant
     * to authentication, keeping the network layer modular.
     */
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}