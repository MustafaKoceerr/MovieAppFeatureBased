package com.mustafakocer.feature_auth.di

import com.mustafakocer.core_domain.provider.SessionProvider
import com.mustafakocer.feature_auth.account.domain.repository.AccountRepository
import com.mustafakocer.feature_auth.shared.data.repository.AuthRepositoryImpl
import com.mustafakocer.feature_auth.welcome.domain.repository.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl,
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        impl: AuthRepositoryImpl,
    ): AccountRepository

}