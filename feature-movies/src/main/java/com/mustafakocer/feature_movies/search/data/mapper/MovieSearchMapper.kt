package com.mustafakocer.feature_movies.search.data.mapper

import com.mustafakocer.feature_movies.shared.data.dto.MovieDto
import com.mustafakocer.feature_movies.shared.domain.model.Movie


/**
 * Mapper for search functionality
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Data Mapping
 * RESPONSIBILITY: Convert API DTOs to Domain Models
 */

/**
 * Convert MovieDto from search API to Movie domain model
 */
fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate.orEmpty(),
        voteAverage = voteAverage ?: 0.0,
        genreIds = genreIds ?: emptyList(),
        voteCount = voteCount ?: 0,
        adult = adult ?: false
    )
}


/**
 * Convert list of MovieDto to list of Movie
 */
fun List<MovieDto>.toDomainMovies(): List<Movie> {
    return map { it.toDomain() }
}