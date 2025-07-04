package com.mustafakocer.feature_movies.list.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mustafakocer.core_database.cache.CacheMetadata
import com.mustafakocer.database_contracts.MovieEntityContract

/**
 * Movie list entity for pagination
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Data Storage
 * EXTENDS: Core database CacheMetadata for cache management
 *
 *  * DATABASE CONTRACTS PATTERN:
 *  * ✅ Implements MovieEntityContract interface
 *  * ✅ Room entity for actual database storage
 *  * ✅ Composition with core CacheMetadata
 */
@Entity(tableName = "movie_list_items")
data class MovieListEntity(
    @PrimaryKey
    override val id: Int,
    override val title: String,
    override val overview: String,
    override val posterPath: String?,
    override val backdropPath: String?,
    override val releaseDate: String,
    override val voteAverage: Double,
    override val genreIds: String, // JSON string of List<Int>
    override val adult: Boolean,
    override val originalLanguage: String,
    override val originalTitle: String,
    override val popularity: Double,
    override val video: Boolean,
    override val voteCount: Int,

    // Pagination metadata
    override val category: String,
    override val page: Int,
    override val position: Int,

    // Cache metadata via composition
    @Embedded(prefix = "movie_cache_") // ✅ Prefix eklendi
    private val cacheMetadata: CacheMetadata,
) : MovieEntityContract {

    // Implement CacheAwareEntityContract via delegation
    override val cachedAt: Long get() = cacheMetadata.cachedAt
    override val expiresAt: Long get() = cacheMetadata.expiresAt
    override val cacheVersion: Int get() = cacheMetadata.cacheVersion
    override val isPersistent: Boolean get() = cacheMetadata.isPersistent

    companion object {
        /**
         * Create cache metadata for movie list item
         */

        fun createCacheMetadata(
            category: String,
            page: Int,
            cacheTimeoutHours: Long = 24,
            isPersistent: Boolean = false,
            cacheVersion: Int = 1,
        ): CacheMetadata {
            val cacheTimeoutMs = cacheTimeoutHours * 60 * 60 * 1000L
            return CacheMetadata(
                cachedAt = System.currentTimeMillis(),
                expiresAt = System.currentTimeMillis() + cacheTimeoutMs,
                isPersistent = isPersistent,
                cacheVersion = cacheVersion
            )
        }
    }
}