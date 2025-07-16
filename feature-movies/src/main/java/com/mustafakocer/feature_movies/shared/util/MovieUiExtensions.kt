package com.mustafakocer.feature_movies.shared.util

import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem

/**
 * TEACHING MOMENT: Feature-Specific UI Extensions
 *
 * ✅ NEDEN FEATURE-SPECIFIC:
 * - Movie domain model'ini biliyor
 * - UI logic ama feature'a özel
 * - Gereksiz generic yapı yok
 * - Clean ve focused
 */

// ===== MOVIE UI EXTENSIONS =====

/**
 * Get TMDB poster URL for Movie
 *
 * @param size Image size (w92, w154, w185, w342, w500, w780, original)
 * @return Full poster URL or null if posterPath is null
 */
fun MovieListItem.getPosterUrl(size: String = "w342"): String? {
    val url = posterUrl?.let { path ->
        "https://image.tmdb.org/t/p/$size$path"
    }
    println(url)
    return url
}

/**
 * Get TMDB backdrop URL for Movie
 *
 * @param size Image size (w300, w780, w1280, original)
 * @return Full backdrop URL or null if backdropPath is null
 */
fun MovieListItem.getBackdropUrl(size: String = "w780"): String? {
    return getBackdropUrl()?.let {
        "https://image.tmdb.org/t/p/$size$it"
    }
}

/**
 * Format rating for UI display
 */
val MovieListItem.formattedRating: String
    get() = String.format("%.1f", voteAverage)

/**
 * Check if movie has high rating
 */
val MovieListItem.isHighRated: Boolean
    get() = voteAverage >= 7.0


/**
 * Check if poster is available
 */
val MovieListItem.hasValidPoster: Boolean
    get() = !posterUrl.isNullOrBlank()

