package com.mustafakocer.movieappfeaturebasedclean.di


import com.mustafakocer.core_domain.config.NetworkConfigProvider
import com.mustafakocer.movieappfeaturebasedclean.config.MovieNetworkConfigProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    /**
     * Hilt'e, birisi NetworkConfigProvider istediğinde,
     * MovieNetworkConfigProvider'ın bir örneğini vermesi gerektiğini söyler.
     * @Binds, bir interface'i somut bir implementasyonuna bağlamanın en verimli yoludur.
     */
    @Binds
    @Singleton
    abstract fun bindNetworkConfigProvider(
        provider: MovieNetworkConfigProvider
    ): NetworkConfigProvider
}
