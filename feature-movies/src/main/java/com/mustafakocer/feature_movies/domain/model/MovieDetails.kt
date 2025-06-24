package com.mustafakocer.feature_movies.domain.model

import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString

/**
 * Detailed movie information (for detail screen)
 */
/**
 * Detailed movie information
 * Sadece detail screen için gerekli extra bilgiler
 */
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val runtime: Int?,
    val genres: List<Genre>,
    val tagline: String?
)
