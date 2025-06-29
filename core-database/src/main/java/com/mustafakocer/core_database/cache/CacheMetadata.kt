package com.mustafakocer.core_database.cache

import androidx.room.ColumnInfo

/**
 * Cache metadata for embedded cache information
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Data Storage
 * RESPONSIBILITY: Store cache state data ONLY (no business logic)
 *
 * DESIGN PATTERN: Value Object
 * - Immutable data container
 * - Only computed properties (no side effects)
 * - No business logic methods
 */
data class CacheMetadata(
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long,

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long,

    @ColumnInfo(name = "cache_version")
    val cacheVersion: Int = 1,

    @ColumnInfo(name = "is_persistent")
    val isPersistent: Boolean = false
) {
    // ✅ ALLOWED: Pure computed properties (no side effects)

    /**
     * Check if cache is still valid
     */
    val isValid: Boolean
        get() = isPersistent || System.currentTimeMillis() < expiresAt

    /**
     * Check if cache is expired
     */
    val isExpired: Boolean
        get() = !isValid

    /**
     * Get cache age in milliseconds
     */
    val ageInMillis: Long
        get() = System.currentTimeMillis() - cachedAt

    /**
     * Get cache age in minutes
     */
    val ageInMinutes: Long
        get() = ageInMillis / (60 * 1000L)

    /**
     * Get cache age in hours
     */
    val ageInHours: Long
        get() = ageInMillis / (60*60*1000L)

    /**
     * Get cache age in days
     */
    val ageInDays: Long
        get() = ageInMillis / (24*60*60*1000L)

    companion object{
        // Cache duration constants
        const val DEFAULT_CACHE_DURATION = 60 * 60 * 1000L      // 1 hour
        const val SHORT_CACHE_DURATION = 5 * 60 * 1000L         // 5 minutes
        const val MEDIUM_CACHE_DURATION = 30 * 60 * 1000L       // 30 minutes
        const val LONG_CACHE_DURATION = 24 * 60 * 60 * 1000L    // 24 hours
        const val WEEK_CACHE_DURATION = 7 * 24 * 60 * 60 * 1000L // 7 days
    }
}