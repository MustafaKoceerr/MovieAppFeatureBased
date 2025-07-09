package com.mustafakocer.feature_movies.search.di

import com.mustafakocer.feature_movies.search.data.repository.SearchRepositoryImpl
import com.mustafakocer.feature_movies.search.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Search feature DI module
 *
 * RESPONSIBILITY: Provide search dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SearchRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        searchRepositoryImpl: SearchRepositoryImpl,
    ): SearchRepository
}