package com.mustafakocer.feature_movies.shared.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import com.mustafakocer.core_database.cache.CacheMetadata

/** Bu entity'de pagination olmayacağı için, kodlar pagination ile karışmasın, pagination sistemini bozmasın diye home screen için yeni entity oluşturduk
 */

@Entity(
    tableName = "home_movies",
    primaryKeys = ["id", "category", "language"] // <-- BİLEŞİK ANAHTAR
)
data class HomeMovieEntity(
    val id: Int,
    val language: String,
    val category: String, // "popular", "upcoming", etc.

    // Core movie data
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val genreIds: List<Int>?,
    val adult: Boolean?,
    val popularity: Double?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val video: Boolean?,

    // Cache metadata
    @Embedded(prefix = "home_movie_cache_")
    val cacheMetadata: CacheMetadata,
)