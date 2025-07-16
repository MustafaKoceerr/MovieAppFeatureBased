package com.mustafakocer.feature_movies.shared.util

import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import java.util.Locale

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
val Double.formattedRating: String
    get() = String.format(Locale.getDefault(), "%.1f", this)

