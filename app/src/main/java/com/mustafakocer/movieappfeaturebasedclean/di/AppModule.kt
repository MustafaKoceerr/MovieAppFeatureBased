package com.mustafakocer.movieappfeaturebasedclean.di


import com.mustafakocer.core_common.config.NetworkConfigProvider
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
    @Singleton // MovieNetworkConfigProvider @Singleton olduğu için bu da @Singleton olmalı
    abstract fun bindNetworkConfigProvider(
        provider: MovieNetworkConfigProvider
    ): NetworkConfigProvider
}
