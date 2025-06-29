package com.mustafakocer.core_database.di

import com.mustafakocer.core_database.cache.CacheService
import com.mustafakocer.core_database.pagination.PaginationSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * Core database dependency injection module
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Dependency Injection
 * RESPONSIBILITY: Provide core database services and configurations
 *
 * DESIGN PATTERN: Dependency Injection
 * - Centralized service provisioning
 * - Singleton lifecycle management
 * - Configuration management
 */

@Module
@InstallIn(SingletonComponent::class)
object CoreDatabaseModule {

    /**
     * Provide JSON serializer for type converters
     */
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            isLenient = true
            allowStructuredMapKeys = true
            prettyPrint = false // For performance in production
        }
    }

    /**
     * Provide cache service
     * Already annotated with @Singleton in CacheService class
     */
    @Provides
    @Singleton
    fun provideCacheService(): CacheService {
        return CacheService()
    }

    /**
     * Provide default pagination settings
     */
    @Provides
    @Singleton
    fun provideDefaultPaginationSettings(): PaginationSettings {
        return PaginationSettings.default
    }

    /**
     * Provide network-optimized pagination settings
     */
    @Provides
    @NetworkOptimized
    fun provideNetworkOptimizedPaginationSettings(): PaginationSettings {
        return PaginationSettings.networkOptimized
    }

    /**
     * Provide database-optimized pagination settings
     */
    @Provides
    @DatabaseOptimized
    fun provideDatabaseOptimizedPaginationSettings(): PaginationSettings {
        return PaginationSettings.databaseOptimized
    }

    /**
     * Provide memory-optimized pagination settings
     */
    @Provides
    @MemoryOptimized
    fun provideMemoryOptimizedPaginationSettings(): PaginationSettings {
        return PaginationSettings.memoryOptimized
    }
}