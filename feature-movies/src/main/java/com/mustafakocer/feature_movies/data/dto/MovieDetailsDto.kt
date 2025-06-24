package com.mustafakocer.feature_movies.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB Movie Details API response
 */
@Serializable
data class MovieDetailsDto(
    @SerialName("id")
    val id: Int,

    @SerialName("title")
    val title: String? = null,

    @SerialName("overview")
    val overview: String? = null,

    @SerialName("poster_path")
    val posterPath: String? = null,

    @SerialName("backdrop_path")
    val backdropPath: String? = null,

    @SerialName("release_date")
    val releaseDate: String? = null,

    @SerialName("vote_average")
    val voteAverage: Double? = null,

    @SerialName("runtime")
    val runtime: Int? = null,

    @SerialName("genres")
    val genres: List<GenreDto>? = null,

    @SerialName("tagline")
    val tagline: String? = null,
)