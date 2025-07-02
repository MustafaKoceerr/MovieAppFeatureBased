package com.mustafakocer.feature_movies.details.di

import com.mustafakocer.feature_movies.details.data.remote.MovieDetailsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideMovieDetailsApiService(retrofit: Retrofit): MovieDetailsApiService =
        retrofit.create(MovieDetailsApiService::class.java)

    @Provides
    @Named("tmdb_api_key")
    // TODO API KEY'I KALDIR
    fun provideApiKey(): String = "60cdc972a61d74fa8a6d7e21cc4a968f"
}