package com.mustafakocer.movieappfeaturebasedclean.di

import android.content.Context
import com.mustafakocer.core_common.config.NetworkConfigProvider
import com.mustafakocer.core_network.config.NetworkConfig
import com.mustafakocer.movieappfeaturebasedclean.config.MovieNetworkConfigProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import java.io.File
import javax.inject.Singleton

/**
 * App-specific network configurations
 *
 * ✅ Provides app-specific NetworkConfigProvider
 * ✅ Provides HTTP cache with proper directory
 * ✅ Overrides core-network's null cache provider
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppNetworkModule {

    @Binds
    @Singleton
    abstract fun bindNetworkConfigProvider(
        movieNetworkConfigProvider: MovieNetworkConfigProvider,
    ): NetworkConfigProvider

    companion object {
        @Provides
        @Singleton
        fun provideCache(
            @ApplicationContext context: Context,
        ): Cache {
            val cacheDir = File(context.cacheDir, "http_cache")
            return Cache(cacheDir, NetworkConfig.CACHE_SIZE)
        }
    }
}