package com.mustafakocer.movieappfeaturebasedclean.di

import com.mustafakocer.database_contracts.DatabaseEntityRegistry
import com.mustafakocer.movieappfeaturebasedclean.database.AppDatabaseRegistry
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Simplified database dependency injection module
 *
 * For now, we're focusing on getting Room working correctly
 * Dynamic registry can be added later once basic functionality works
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseRegistryModule {
    // Registry implementation temporarily removed
    // Focus on getting Room database working first

    // TODO: Add dynamic registry back once Room compilation works
    // @Binds
    // @Singleton
    // abstract fun bindDatabaseEntityRegistry(
    //     appDatabaseRegistry: AppDatabaseRegistry
    // ): DatabaseEntityRegistry
}