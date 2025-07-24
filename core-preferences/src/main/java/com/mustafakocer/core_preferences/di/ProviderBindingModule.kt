package com.mustafakocer.core_preferences.di

import com.mustafakocer.core_domain.provider.SessionProvider
import com.mustafakocer.core_preferences.provider.DefaultSessionProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * A Hilt module for binding interfaces from the domain layer to their concrete implementations
 * in this data layer.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesBindingsModule {

    @Binds
    @Singleton
    abstract fun bindSessionProvider(impl: DefaultSessionProvider): SessionProvider

}
