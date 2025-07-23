package com.mustafakocer.core_database.cache

import androidx.room.ColumnInfo

/**
 * A reusable data class holding metadata for cached entities.
 *
 * Architectural Note:
 * This class is designed to be embedded directly into Room entities using the `@Embedded`
 * annotation. This approach standardizes cache management across all tables and cleanly
 * separates caching-related fields (like timestamps) from the core data model of the entity.
 *
 * @property cachedAt The timestamp (in milliseconds) when the entity was saved.
 * @property expiresAt The timestamp (in milliseconds) when the entity should be considered stale.
 * @property cacheVersion A version number for invalidating data if the schema changes.
 * @property isPersistent A flag to prevent data from being automatically cleared.
 */
data class CacheMetadata(
    @ColumnInfo(name = "cached_at")
    val cachedAt: Long,

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long,

    @ColumnInfo(name = "cache_version")
    val cacheVersion: Int = 1,

    @ColumnInfo(name = "is_persistent")
    val isPersistent: Boolean = false,
) {
    companion object {

        fun create(durationMillis: Long): CacheMetadata {
            val now = System.currentTimeMillis()
            return CacheMetadata(
                cachedAt = now,
                expiresAt = now + durationMillis
            )
        }
    }
}