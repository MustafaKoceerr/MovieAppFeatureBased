package com.mustafakocer.feature_movies.di

import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

/**
 * A Dagger Hilt module for providing network-related dependencies specific to the movies feature.
 *
 * Architectural Decision: This module is responsible for creating the concrete implementation of
 * the `MovieApiService` interface. It leverages a pre-configured `Retrofit` instance that is
 * provided by a lower-level module (e.g., a `core-network` module). This is a powerful feature
 * of Hilt's dependency graph: this `feature-movies` module does not need to know *how* the `Retrofit`
 * instance is built (e.g., with what base URL, interceptors, or converters). It simply declares
 * its dependency on `Retrofit`, and Hilt resolves it from the appropriate provider in the graph.
 * This promotes excellent separation of concerns, keeping network configuration details isolated
 * from the feature modules that consume the API services.
 */
@Module
@InstallIn(SingletonComponent::class)
object MovieNetworkModule {

    @Provides
    @Singleton
    fun provideMovieApiService(
        retrofit: Retrofit,
    ): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }
}