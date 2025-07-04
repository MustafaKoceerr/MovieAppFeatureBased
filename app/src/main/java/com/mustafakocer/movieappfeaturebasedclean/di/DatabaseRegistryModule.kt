package com.mustafakocer.movieappfeaturebasedclean.di

import com.mustafakocer.movieappfeaturebasedclean.database.AppDatabaseRegistry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Simplified database dependency injection module
 *
 * MANUAL APPROACH:
 * ✅ Simple registry provider
 * ✅ No complex multibinding
 * ✅ Focus on getting Room working correctly
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseRegistryModule {

    /**
     * Provide database registry for debugging and monitoring
     */
    @Provides
    @Singleton
    fun provideAppDatabaseRegistry(
        appDatabaseRegistry: AppDatabaseRegistry
    ): AppDatabaseRegistry = appDatabaseRegistry
}