// feature-movies/src/main/java/com/mustafakocer/feature_movies/list/data/local/entity/MovieListEntity.kt
package com.mustafakocer.feature_movies.list.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mustafakocer.core_database.cache.CacheMetadata

/**
 * Movie list entity for pagination
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Data Storage
 * EXTENDS: Core database CacheMetadata for cache management
 *
 * DATABASE CONTRACTS PATTERN:
 * ✅ Room entity for actual database storage
 * ✅ Composition with core CacheMetadata
 */
@Entity(tableName = "movie_list_items")
data class MovieListEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val genreIds: String, // JSON string of List<Int>
    val adult: Boolean,
    val originalLanguage: String,
    val originalTitle: String,
    val popularity: Double,
    val video: Boolean,
    val voteCount: Int,

    // Pagination metadata
    val category: String,
    val page: Int,
    val position: Int,

    // Cache metadata via composition
    @Embedded(prefix = "movie_cache_")
    val cacheMetadata: CacheMetadata,
)