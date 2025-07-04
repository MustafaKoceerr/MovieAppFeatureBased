package com.mustafakocer.feature_movies.list.di

import com.mustafakocer.feature_movies.list.data.repository.MovieListRepositoryImpl
import com.mustafakocer.feature_movies.list.domain.repository.MovieListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


/**
 * Movie List Repository DI Module
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - DI Configuration
 * RESPONSIBILITY: Bind repository contracts to implementations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class MovieListRepositoryModule {

    // ==================== REPOSITORY BINDING ====================

    /**
     * Bind MovieListRepository interface to implementation
     */
    @Binds
    @Singleton
    abstract fun bindMovieListRepository(
        movieListRepositoryImpl: MovieListRepositoryImpl,
    ): MovieListRepository
}