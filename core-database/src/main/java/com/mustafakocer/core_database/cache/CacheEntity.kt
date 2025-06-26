package com.mustafakocer.core_database.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Base cache operations

/**
 * TEACHING MOMENT: Generic Cache Entity
 *
 * Bu abstract class tüm cache entity'lerin base'i:
 * ✅ Automatic timestamp management
 * ✅ Cache expiration logic
 * ✅ Generic key support
 */

@Entity
abstract class CacheEntity<T> {
    @PrimaryKey
    abstract val cacheKey: String

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis()

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long = System.currentTimeMillis() + DEFAULT_CACHE_DURATION

    /**
     * Check if cache is still valid
     */
    val isValid: Boolean
        get() = System.currentTimeMillis() < expiresAt

    /**
     * Get the actual data (to be implemented by subclasses)
     */
    abstract fun getData(): T

    companion object {
        const val DEFAULT_CACHE_DURATION = 60 * 60 * 1000L // 1 hour
    }
}