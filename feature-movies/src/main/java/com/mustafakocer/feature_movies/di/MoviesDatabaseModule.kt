package com.mustafakocer.feature_movies.di

import com.mustafakocer.feature_movies.database.MoviesDatabaseInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Movies feature database DI module
 *
 * SIMPLIFIED APPROACH:
 * ✅ Provides database info for manual registration
 * ✅ No dynamic registration complexity
 * ✅ Focus on getting basic functionality working first
 */
@Module
@InstallIn(SingletonComponent::class)
object MoviesDatabaseModule {

    /**
     * Provide movies database info
     * Used by app module for manual entity registration
     */
    @Provides
    @Singleton
    fun provideMoviesDatabaseInfo(): MoviesDatabaseInfo {
        return MoviesDatabaseInfo()
    }
}