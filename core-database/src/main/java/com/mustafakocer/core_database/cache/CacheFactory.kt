package com.mustafakocer.core_database.cache

/**
 * Factory for common cache configurations
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Factory
 * RESPONSIBILITY: Provide convenient cache creation methods
 *
 * DESIGN PATTERN: Factory Pattern
 * - Static factory methods
 * - Convenience constructors
 * - Commonly used configurations
 */
object CacheFactory {

    /**
     * Create short-term cache (5 minutes)
     * Use for: Frequently changing data, user interactions
     */
    fun shortTerm(): CacheMetadata {
        val now = System.currentTimeMillis()
        return CacheMetadata(
            cachedAt = now,
            expiresAt = now + CacheMetadata.SHORT_CACHE_DURATION,
            cacheVersion = 1,
            isPersistent = false
        )
    }

    /**
     * Create medium-term cache (30 minutes)
     * Use for: API responses, search results
     */
    fun mediumTerm(): CacheMetadata {
        val now = System.currentTimeMillis()
        return CacheMetadata(
            cachedAt = now,
            expiresAt = now + CacheMetadata.MEDIUM_CACHE_DURATION,
            cacheVersion = 1,
            isPersistent = false
        )
    }

    /**
     * Create long-term cache (24 hours)
     * Use for: Static content, configuration data
     */
    fun longTerm(): CacheMetadata {
        val now = System.currentTimeMillis()
        return CacheMetadata(
            cachedAt = now,
            expiresAt = now + CacheMetadata.LONG_CACHE_DURATION,
            cacheVersion = 1,
            isPersistent = false
        )
    }

    /**
     * Create weekly cache (7 days)
     * Use for: Very static content, offline data
     */
    fun weekly(): CacheMetadata {
        val now = System.currentTimeMillis()
        return CacheMetadata(
            cachedAt = now,
            expiresAt = now + CacheMetadata.WEEK_CACHE_DURATION,
            cacheVersion = 1,
            isPersistent = false
        )
    }

    /**
     * Create persistent cache (never expires)
     * Use for: User preferences, downloaded content
     */
    fun persistent(): CacheMetadata {
        return CacheMetadata(
            cachedAt = System.currentTimeMillis(),
            expiresAt = Long.MAX_VALUE,
            cacheVersion = 1,
            isPersistent = true
        )
    }

    /**
     * Create cache with custom duration
     * Use for: Special requirements
     */
    fun withDuration(durationMillis: Long): CacheMetadata {
        val now = System.currentTimeMillis()
        return CacheMetadata(
            cachedAt = now,
            expiresAt = now + durationMillis,
            cacheVersion = 1,
            isPersistent = false
        )
    }

    /**
     * Create cache with custom duration in minutes
     * Use for: Easy configuration
     */
    fun withMinutes(minutes: Int): CacheMetadata {
        return withDuration(minutes * 60 * 1000L)
    }

    /**
     * Create cache with custom duration in hours
     * Use for: Easy configuration
     */
    fun withHours(hours: Int): CacheMetadata {
        return withDuration(hours * 60 * 60 * 1000L)
    }

    /**
     * Create cache with custom duration in days
     * Use for: Easy configuration
     */
    fun withDays(days: Int): CacheMetadata {
        return withDuration(days * 24 * 60 * 60 * 1000L)
    }

    /**
     * Create default cache (1 hour)
     * Use for: Most common use cases
     */
    fun default(): CacheMetadata {
        val now = System.currentTimeMillis()
        return CacheMetadata(
            cachedAt = now,
            expiresAt = now + CacheMetadata.DEFAULT_CACHE_DURATION,
            cacheVersion = 1,
            isPersistent = false
        )
    }
}