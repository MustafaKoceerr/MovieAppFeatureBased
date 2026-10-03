package com.mustafakocer.feature_movies.di

import com.mustafakocer.feature_movies.details.data.repository.MovieDetailsRepositoryImpl
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import com.mustafakocer.feature_movies.home.data.repository.HomeRepositoryImpl
import com.mustafakocer.feature_movies.home.domain.repository.HomeRepository
import com.mustafakocer.feature_movies.list.data.repository.MovieListRepositoryImpl
import com.mustafakocer.feature_movies.list.domain.repository.MovieListRepository
import com.mustafakocer.feature_movies.search.data.repository.SearchRepositoryImpl
import com.mustafakocer.feature_movies.search.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * A Dagger Hilt module responsible for providing repository implementations.
 *
 * Architectural Decision: This module uses the `@Binds` annotation, which is a more efficient
 * way to provide an implementation for an interface compared to `@Provides`. `@Binds` tells Hilt
 * that whenever a dependency requests an interface (e.g., `HomeRepository`), it should provide
 * the specified concrete implementation (`HomeRepositoryImpl`). This is a key part of dependency
 * injection and Clean Architecture, as it allows the rest of the application (like ViewModels
 * and UseCases) to depend on abstract interfaces rather than concrete implementations, promoting
 * loose coupling and testability.
 *
 * The module is installed in `SingletonComponent`, which means that Hilt will create a single,
 * application-wide instance of each repository. This is appropriate for repositories as they are
 * typically stateless and can be shared across the entire app.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieDetailsRepository(impl: MovieDetailsRepositoryImpl): MovieDetailsRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindMovieListRepository(impl: MovieListRepositoryImpl): MovieListRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository
}