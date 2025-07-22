package com.mustafakocer.core_preferences.di

import com.mustafakocer.core_domain.provider.SessionProvider
import com.mustafakocer.core_preferences.provider.DefaultSessionProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProviderBindingModule { // <-- 'abstract class' olmak zorunda

    @Binds
    @Singleton
    abstract fun bindSessionProvider(
        defaultSessionProvider: DefaultSessionProvider,
    ): SessionProvider
}