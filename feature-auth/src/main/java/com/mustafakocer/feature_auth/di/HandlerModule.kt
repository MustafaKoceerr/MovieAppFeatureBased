package com.mustafakocer.feature_auth.di

import com.mustafakocer.feature_auth.welcome.data.handler.AuthCallbackHandler
import com.mustafakocer.feature_auth.welcome.domain.provider.AuthCallbackProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HandlerModule {

    @Binds
    @Singleton
    abstract fun bindAuthCallbackProvider(
        handler: AuthCallbackHandler,
    ): AuthCallbackProvider
}