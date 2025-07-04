package com.mustafakocer.feature_movies.list.domain.model

/**
 * Movie list item for pagination screens
 *
 * CLEAN ARCHITECTURE: Domain Layer
 * RESPONSIBILITY: Business entity for movie list display
 */
data class MovieListItem(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val genreIds: List<Int> = emptyList(),
    val adult: Boolean = false,
    val originalLanguage: String = "",
    val originalTitle: String = "",
    val popularity: Double = 0.0,
    val video: Boolean = false,
    val voteCount: Int = 0
)