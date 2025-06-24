package com.mustafakocer.feature_movies.data.mapper

import com.mustafakocer.feature_movies.data.dto.MovieDetailsDto
import com.mustafakocer.feature_movies.domain.model.MovieDetails

/**
 * Convert MovieDetailsDto to Domain MovieDetails
 */
fun MovieDetailsDto.toDomainModel(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate.orEmpty(),
        voteAverage = voteAverage ?: 0.0,
        runtime = runtime,
        genres = genres?.mapNotNull { it.toDomainModel() }.orEmpty(),
        tagline = tagline
    )
}