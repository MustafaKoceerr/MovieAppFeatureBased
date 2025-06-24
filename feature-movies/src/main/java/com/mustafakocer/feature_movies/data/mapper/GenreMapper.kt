package com.mustafakocer.feature_movies.data.mapper

import com.mustafakocer.feature_movies.data.dto.GenreDto
import com.mustafakocer.feature_movies.domain.model.Genre

/**
 * Convert GenreDto to Domain Genre
 */
fun GenreDto.toDomainModel(): Genre? {
    // Genre name boşsa null döndür (filter out)
    return if (name.isNullOrBlank()) null
    else {
        Genre(
            id = id,
            name = name
        )
    }
}