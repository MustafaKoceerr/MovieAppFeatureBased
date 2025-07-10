package com.mustafakocer.feature_movies.shared.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Movie list item DTO from TMDB API
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer
 * RESPONSIBILITY: API response structure mapping
 * */
@Serializable
data class MovieListDto(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("backdrop_path")
    val backdropPath: String?,
    @SerialName("release_date")
    val releaseDate: String,
    @SerialName("vote_average")
    val voteAverage: Double,
    @SerialName("genre_ids")
    val genreIds: List<Int> = emptyList(),
    val adult: Boolean = false,
    @SerialName("original_language")
    val originalLanguage: String = "",
    @SerialName("original_title")
    val originalTitle: String = "",
    val popularity: Double = 0.0,
    val video: Boolean = false,
    @SerialName("vote_count")
    val voteCount: Int = 0,
)