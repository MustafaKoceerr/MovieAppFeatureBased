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