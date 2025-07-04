package com.mustafakocer.feature_movies.list.di

import com.mustafakocer.feature_movies.list.data.remote.MovieListApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MovieListApiModule {

    /**
     * Provide MovieListApiService from Retrofit
     */
    @Provides
    @Singleton
    fun provideMovieListApiService(
        retrofit: Retrofit,
    ): MovieListApiService {
        return retrofit.create(MovieListApiService::class.java)
    }
}