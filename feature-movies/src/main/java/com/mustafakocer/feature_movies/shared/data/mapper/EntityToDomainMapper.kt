package com.mustafakocer.feature_movies.shared.data.mapper

import com.mustafakocer.feature_movies.shared.data.local.entity.HomeMovieEntity
import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem

// ============================================
// 1. ENTITY -> DOMAIN MAPPERS
// ============================================

// MovieListEntity -> MovieListItem (Domain)
fun MovieListEntity.toDomainList(): MovieListItem {
    return MovieListItem(
        id = id,
        title = title?.takeIf { it.isNotBlank() } ?: "Unknown Title",
        overview = overview?.takeIf { it.isNotBlank() } ?: "No overview available",
        posterUrl = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
        releaseYear = releaseDate?.takeIf { it.isNotBlank() }?.let { date ->
            try {
                date.substring(0, 4)
            } catch (e: Exception) {
                "Unknown"
            }
        } ?: "Unknown",
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0
    )
}


// HomeMovieEntity -> MovieListItem (Domain)
fun HomeMovieEntity.toDomainList(): MovieListItem {
    return MovieListItem(
        id = id,
        title = title?.takeIf { it.isNotBlank() } ?: "Unknown Title",
        overview = overview?.takeIf { it.isNotBlank() } ?: "No overview available",
        posterUrl = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
        releaseYear = releaseDate?.takeIf { it.isNotBlank() }?.let { date ->
            try {
                date.substring(0, 4)
            } catch (e: Exception) {
                "Unknown"
            }
        } ?: "Unknown",
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0
    )
}