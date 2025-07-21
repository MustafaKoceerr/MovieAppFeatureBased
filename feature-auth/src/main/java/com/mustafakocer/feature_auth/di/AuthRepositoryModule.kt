package com.mustafakocer.feature_auth.di
import com.mustafakocer.core_common.provider.SessionProvider
import com.mustafakocer.feature_auth.data.repository.AuthRepositoryImpl
import com.mustafakocer.feature_auth.domain.repository.AuthRepository
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
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    // YENİ BINDING:
    // Birisi SessionProvider istediğinde, AuthRepository implementasyonunu ver.
    @Binds
    @Singleton
    abstract fun bindSessionProvider(
        authRepository: AuthRepository
    ): SessionProvider
}