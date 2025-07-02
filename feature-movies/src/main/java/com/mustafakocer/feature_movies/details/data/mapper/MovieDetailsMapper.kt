package com.mustafakocer.feature_movies.details.data.mapper

import com.mustafakocer.feature_movies.details.data.remote.dto.GenreDto
import com.mustafakocer.feature_movies.details.data.remote.dto.MovieDetailsDto
import com.mustafakocer.feature_movies.details.domain.model.Genre
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails

/**
 * Movie details mapper
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer
 * RESPONSIBILITY: Convert between DTO and Domain models
 */

/**
 * Convert MovieDetailsDto to MovieDetails domain model
 */
fun MovieDetailsDto.toDomain(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        runtime = runtime,
        genres = genres.map { it.toDomain() },
        tagline = tagline
    )
}

/**
 * Convert GenreDto to Genre domain model
 */
fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

/**
 * Convert list of MovieDetailsDto to list of MovieDetails
 */
fun List<MovieDetailsDto>.toDomain(): List<MovieDetails> {
    return map { it.toDomain() }
}

/**
 * Convert list of GenreDto to list of Genre
 */
fun List<GenreDto>.toGenreDomain(): List<Genre> {
    return map { it.toDomain() }
}