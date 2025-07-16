package com.mustafakocer.feature_movies.shared.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mustafakocer.core_database.cache.CacheMetadata

/**
 * Room Entity for storing movie list items with pagination and cache metadata.
 * Based on MovieDto structure with additional database-specific fields.
 */
@Entity(tableName = "movie_list")
data class MovieListEntity(
    @PrimaryKey
    val id: Int,

    // Core movie data from MovieDto
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val genreIds: List<Int>?, // TypeConverter handle eder
    val adult: Boolean?,
    val popularity: Double?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val video: Boolean?,

    // Pagination metadata
    val category: String, // "popular", "upcoming", "now_playing", "top_rated", "search"
    val page: Int,        // Hangi sayfadan geldi
    val position: Int,    // Sayfa içindeki pozisyon (0-based)

    // Cache metadata via composition
    @Embedded(prefix = "movie_cache_")
    val cacheMetadata: CacheMetadata
)
