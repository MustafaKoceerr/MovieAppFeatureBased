package com.mustafakocer.feature_auth.di

import com.mustafakocer.feature_auth.account.domain.repository.AccountRepository
import com.mustafakocer.feature_auth.shared.data.repository.AuthRepositoryImpl
import com.mustafakocer.feature_auth.welcome.domain.repository.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * A Hilt module for binding repository interfaces within the authentication feature
 * to their concrete implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        authRepositoryImpl: AuthRepositoryImpl,
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        impl: AuthRepositoryImpl,
    ): AccountRepository
}