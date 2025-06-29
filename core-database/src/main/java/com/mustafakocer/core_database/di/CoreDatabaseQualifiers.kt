package com.mustafakocer.core_database.di

import javax.inject.Qualifier

/**
 * Qualifier annotations for different pagination settings
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkOptimized

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseOptimized

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MemoryOptimized

/**
 * Additional qualifiers for cache configurations
 */

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ShortTermCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LongTermCache

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PersistentCache

