package com.mustafakocer.movieappfeaturebasedclean.di

import com.mustafakocer.core_domain.config.NetworkConfigProvider
import com.mustafakocer.movieappfeaturebasedclean.config.MovieNetworkConfigProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * The main application-level Dagger Hilt module.
 *
 * This module is responsible for providing application-wide dependencies, particularly for
 * binding abstract interfaces defined in lower-level modules (like `core-domain`) to their
 * concrete implementations that reside within this `:app` module.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindNetworkConfigProvider(
        provider: MovieNetworkConfigProvider,
    ): NetworkConfigProvider
}