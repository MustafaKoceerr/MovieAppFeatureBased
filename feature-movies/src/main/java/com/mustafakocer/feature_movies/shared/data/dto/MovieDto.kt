package com.mustafakocer.feature_movies.shared.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB Movie API response DTO
 */
@Serializable
data class MovieDto(
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

    @SerialName("genre_ids")
    val genreIds: List<Int>? = null,

    @SerialName("vote_count")
    val voteCount: Int? = null,

    @SerialName("adult")
    val adult: Boolean? = null
)
