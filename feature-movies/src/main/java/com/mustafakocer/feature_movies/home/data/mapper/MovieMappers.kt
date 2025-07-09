package com.mustafakocer.feature_movies.home.data.mapper

import com.mustafakocer.feature_movies.shared.data.dto.MovieDto
import com.mustafakocer.feature_movies.home.domain.model.Genre
import com.mustafakocer.feature_movies.shared.domain.model.Movie

/**
 * TEACHING MOMENT: Clean Data Mapping
 *
 * ✅ PRINCIPLES:
 * - Null safety with sensible defaults
 * - Error handling for malformed data
 * - Clean API → Domain conversion
 * - Extension functions for readability
 */

// ==================== MOVIE DTO → DOMAIN ====================

fun MovieDto.toDomainModel(): Movie {
    return Movie(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate.orEmpty(),
        voteAverage = voteAverage ?: 0.0,
        genreIds = genreIds.orEmpty()
    )
}

fun List<MovieDto>.toDomainModels(): List<Movie> {
    return mapNotNull { movieDto ->
        try {
            movieDto.toDomainModel()
        } catch (e: Exception) {
            // Log error in production, skip invalid movie
            null
        }
    }
}


