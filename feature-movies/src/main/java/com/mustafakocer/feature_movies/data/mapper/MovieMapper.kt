package com.mustafakocer.feature_movies.data.mapper

import com.mustafakocer.feature_movies.data.dto.MovieDto
import com.mustafakocer.feature_movies.domain.model.Movie


/**
 * TEACHING MOMENT: Mapping Layer Best Practices
 *
 * ✅ Null safety handling
 * ✅ Default values for required fields
 * ✅ Data validation
 * ✅ Clean API → Domain conversion
 */

/**
 * Convert MovieDto to Domain Movie
 */
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

/**
 * Convert list of MovieDto to list of Domain Movie
 */
fun List<MovieDto>.toDomainModels(): List<Movie> {
    return this.map { it.toDomainModel() }
}