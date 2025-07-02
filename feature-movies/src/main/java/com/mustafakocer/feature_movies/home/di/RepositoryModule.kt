package com.mustafakocer.feature_movies.home.di

import com.mustafakocer.feature_movies.home.data.repository.MovieRepositoryImpl
import com.mustafakocer.feature_movies.home.domain.repository.MovieRepository
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
     abstract fun bindMovieRepository(
         movieRepositoryImpl: MovieRepositoryImpl
     ): MovieRepository
}