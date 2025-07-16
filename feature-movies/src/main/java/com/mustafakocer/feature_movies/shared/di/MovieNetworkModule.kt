package com.mustafakocer.feature_movies.shared.di
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
/**
 * CONSOLIDATED: Single network module for all movie API services
 *
 * ✅ Uses core-network's shared Retrofit instance
 * ✅ Single MovieApiService for all movie operations
 *
 * HILT MAGIC:
 * - Retrofit comes from core-network module
 * - Config comes from app module
 * - feature-movies has no dependency on app module!
 */
@Module
@InstallIn(SingletonComponent::class)
object MovieNetworkModule {
    /**
     * Provide single MovieApiService for all movie operations
     *
     * Uses shared Retrofit instance from core-network
     */
    @Provides
    @Singleton
    fun provideMovieApiService(
        retrofit: Retrofit // ← HILT MAGIC: This comes from core-network!
    ): MovieApiService {
        return retrofit.create(MovieApiService::class.java)
    }

}