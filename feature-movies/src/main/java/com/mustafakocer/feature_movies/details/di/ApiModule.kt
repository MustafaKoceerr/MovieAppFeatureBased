package com.mustafakocer.feature_movies.details.di

import com.mustafakocer.feature_movies.details.data.remote.MovieDetailsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideMovieDetailsApiService(retrofit: Retrofit): MovieDetailsApiService =
        retrofit.create(MovieDetailsApiService::class.java)


}