package com.mustafakocer.feature_movies.domain.model

/**
 * Check if runtime is valid
 */
val MovieDetails.hasValidRuntime: Boolean
    get() = runtime != null && runtime > 0

/**
 * Format runtime for UI display
 */
val MovieDetails.formattedRuntime: String
    get() = runtime?.let {
        val hours = it / 60
        val minutes = it % 60
        when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "Unknown"
        }
    } ?: "Unknown"

/**
 * Check if movie has genres
 */
val MovieDetails.hasGenres: Boolean
    get() = genres.isNotEmpty()

/**
 * Get formatted genre names
 */
val MovieDetails.genreNames: String
    get() = genres.joinToString(", ") { it.name }

/**
 * Check if tagline exists
 */
val MovieDetails.hasTagline: Boolean
    get() = !tagline.isNullOrBlank()