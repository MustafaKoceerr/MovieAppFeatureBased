package com.mustafakocer.feature_movies.details.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Movie details data transfer object
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer
 * RESPONSIBILITY: API response structure mapping
 */
@Serializable
data class MovieDetailsDto(
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
    val runtime: Int?,
    val genres: List<GenreDto> = emptyList(),
    val tagline: String? = null
)