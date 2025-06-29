package com.mustafakocer.core_database.cache

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cache business logic service
 *
 * CLEAN ARCHITECTURE: Application/Domain Layer
 * RESPONSIBILITY: Cache lifecycle management and business rules
 *
 * DESIGN PATTERN: Service Layer
 * - Stateless business logic
 * - Injectable service
 * - Pure functions (testable)
 */
@Singleton
class CacheService @Inject constructor() {

    /**
     * Create new cache metadata with specified duration
     */
    fun createCache(
        durationMillis: Long = CacheMetadata.DEFAULT_CACHE_DURATION,
        isPersistent: Boolean = false
    ): CacheMetadata {
        val now = currentTimeMillis()
        return CacheMetadata(
            cachedAt = now,
            expiresAt = if (isPersistent) Long.MAX_VALUE else now + durationMillis,
            cacheVersion = 1,
            isPersistent = isPersistent
        )
    }

    /**
     * Create short-term cache (5 minutes)
     */
    fun createShortTermCache(): CacheMetadata {
        return createCache(CacheMetadata.SHORT_CACHE_DURATION)
    }

    /**
     * Create medium-term cache (30 minutes)
     */
    fun createMediumTermCache(): CacheMetadata {
        return createCache(CacheMetadata.MEDIUM_CACHE_DURATION)
    }

    /**
     * Create long-term cache (24 hours)
     */
    fun createLongTermCache(): CacheMetadata {
        return createCache(CacheMetadata.LONG_CACHE_DURATION)
    }

    /**
     * Create persistent cache (never expires)
     */
    fun createPersistentCache(): CacheMetadata {
        return createCache(isPersistent = true)
    }

    /**
     * Refresh cache with new timestamp and optional new duration
     */
    fun refresh(
        cache: CacheMetadata,
        newDuration: Long = CacheMetadata.DEFAULT_CACHE_DURATION
    ): CacheMetadata {
        val now = currentTimeMillis()
        return cache.copy(
            cachedAt = now,
            expiresAt = if (cache.isPersistent) Long.MAX_VALUE else now + newDuration,
            cacheVersion = cache.cacheVersion + 1
        )
    }

    /**
     * Extend cache expiry time by additional duration
     */
    fun extendExpiry(
        cache: CacheMetadata,
        additionalDuration: Long
    ): CacheMetadata {
        return if (cache.isPersistent) {
            cache
        } else {
            cache.copy(expiresAt = cache.expiresAt + additionalDuration)
        }
    }

    /**
     * Make cache persistent (never expires)
     */
    fun makePersistent(cache: CacheMetadata): CacheMetadata {
        return cache.copy(
            isPersistent = true,
            expiresAt = Long.MAX_VALUE
        )
    }

    /**
     * Make cache temporary (with expiry time)
     */
    fun makeTemporary(
        cache: CacheMetadata,
        duration: Long = CacheMetadata.DEFAULT_CACHE_DURATION
    ): CacheMetadata {
        val now = currentTimeMillis()
        return cache.copy(
            isPersistent = false,
            expiresAt = now + duration
        )
    }

    /**
     * Check if cache should be refreshed based on business rules
     */
    fun shouldRefresh(
        cache: CacheMetadata,
        forceRefresh: Boolean = false,
        refreshThreshold: Double = 0.8 // Refresh when 80% of cache duration has passed
    ): Boolean {
        return when {
            forceRefresh -> true
            cache.isPersistent -> false
            cache.isExpired -> true
            else -> {
                val cacheDuration = cache.expiresAt - cache.cachedAt
                val ageRatio = cache.ageInMillis.toDouble() / cacheDuration.toDouble()
                ageRatio >= refreshThreshold
            }
        }
    }

    /**
     * Get refresh strategy based on cache state
     */
    fun getRefreshStrategy(
        cache: CacheMetadata,
        backgroundThreshold: Long = CacheMetadata.MEDIUM_CACHE_DURATION
    ): RefreshStrategy {
        return when {
            cache.isPersistent -> RefreshStrategy.NEVER
            cache.isExpired -> RefreshStrategy.IMMEDIATE
            cache.ageInMillis > backgroundThreshold -> RefreshStrategy.BACKGROUND
            else -> RefreshStrategy.NOT_NEEDED
        }
    }

    /**
     * Check if multiple caches need refresh
     */
    fun needsBatchRefresh(
        caches: List<CacheMetadata>,
        threshold: Double = 0.5 // If 50% of caches are stale
    ): Boolean {
        if (caches.isEmpty()) return false

        val staleCount = caches.count { shouldRefresh(it) }
        val staleRatio = staleCount.toDouble() / caches.size.toDouble()

        return staleRatio >= threshold
    }

    /**
     * Get cache health score (0.0 = expired, 1.0 = fresh)
     */
    fun getHealthScore(cache: CacheMetadata): Double {
        return when {
            cache.isPersistent -> 1.0
            cache.isExpired -> 0.0
            else -> {
                val cacheDuration = cache.expiresAt - cache.cachedAt
                val remainingTime = cache.expiresAt - currentTimeMillis()
                (remainingTime.toDouble() / cacheDuration.toDouble()).coerceIn(0.0, 1.0)
            }
        }
    }

    // Protected function for testing (can be mocked)
    protected open fun currentTimeMillis(): Long = System.currentTimeMillis()

    /**
     * Cache refresh strategies
     */
    enum class RefreshStrategy {
        NOT_NEEDED,    // Cache is fresh
        BACKGROUND,    // Cache is aging, refresh in background
        IMMEDIATE,     // Cache is expired, refresh immediately
        NEVER          // Persistent cache, never refresh
    }
}