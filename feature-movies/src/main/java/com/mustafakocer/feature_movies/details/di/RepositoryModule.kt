package com.mustafakocer.feature_movies.details.di

import com.mustafakocer.feature_movies.details.data.repository.MovieDetailsRepositoryImpl
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // ==================== REPOSITORY BINDING ====================
    @Binds
    @Singleton
    abstract fun bindMovieDetailsRepository(
        movieDetailsRepositoryImpl: MovieDetailsRepositoryImpl,
    ): MovieDetailsRepository
}