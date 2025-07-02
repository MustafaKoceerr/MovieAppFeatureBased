package com.mustafakocer.feature_movies.details.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Movie details domain model - UI focused
 *
 * CLEAN ARCHITECTURE: Domain Layer
 * RESPONSIBILITY: Essential movie details for UI display
 */
@Serializable
data class MovieDetails(
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
    val genres: List<Genre> = emptyList(),
    val tagline: String? = null
) {
    // ✅ PURE COMPUTED PROPERTIES (no business logic)

    /**
     * Check if movie has valid poster
     */
    val hasPoster: Boolean
        get() = !posterPath.isNullOrBlank()

    /**
     * Check if movie has valid backdrop
     */
    val hasBackdrop: Boolean
        get() = !backdropPath.isNullOrBlank()

    /**
     * Check if tagline exists
     */
    val hasTagline: Boolean
        get() = !tagline.isNullOrBlank()
}