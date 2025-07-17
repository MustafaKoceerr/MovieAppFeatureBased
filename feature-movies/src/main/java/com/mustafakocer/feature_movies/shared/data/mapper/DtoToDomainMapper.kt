package com.mustafakocer.feature_movies.shared.data.mapper

import com.mustafakocer.feature_movies.shared.data.model.MovieDto
import com.mustafakocer.feature_movies.shared.data.model.MovieDetailsDto
import com.mustafakocer.feature_movies.shared.domain.model.Genre
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem

// ============================================
// 2. DTO -> DOMAIN MAPPERS
// ============================================

// MovieDto -> MovieListItem (Domain)
fun MovieDto.toDomainList(): MovieListItem {
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

// MovieDetailsDto -> MovieDetails (Domain)
fun MovieDetailsDto.toDomain(): MovieDetails {
    return MovieDetails(
        id = id.toInt(),
        title = title?.takeIf { it.isNotBlank() } ?: "",
        overview = overview?.takeIf { it.isNotBlank() } ?: "",
        posterUrl = posterPath?.takeIf { it.isNotBlank() }?.let {
            "https://image.tmdb.org/t/p/w500$it"
        } ?: "",
        backdropUrl = backdropPath?.takeIf { it.isNotBlank() }?.let {
            "https://image.tmdb.org/t/p/w1280$it"
        } ?: "",
        releaseDate = releaseDate?.takeIf { it.isNotBlank() } ?: "",
        voteAverage = voteAverage ?: 0.0,
        runtime = when {
            runtime == null || runtime <= 0 -> ""
            else -> "$runtime min"
        },
        tagline = tagline?.takeIf { it.isNotBlank() } ?: "",
        genres = genres?.mapNotNull { genreDto ->
            genreDto.name?.takeIf { it.isNotBlank() }?.let { name ->
                Genre(id = genreDto.id.toInt(), name = name)
            }
        } ?: listOf()
    )
}
