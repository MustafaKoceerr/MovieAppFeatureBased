package com.mustafakocer.feature_movies.domain.model


/**
 * TEACHING MOMENT: Extension Functions for UI Logic
 *
 * COMPUTED PROPERTIES BURAYA:
 * ✅ Domain model temiz kalır
 * ✅ UI-specific logic ayrılır
 * ✅ Optional: Sadece gerekirse import edilir
 * ✅ Testable: Ayrı ayrı test edilebilir
 */

// ===== MOVIE EXTENSIONS =====

/**
 * Check if movie has high rating
 */
val Movie.isHighRated: Boolean
    get() = voteAverage >= 7.0

/**
 * Check if poster image is available
 */
val Movie.hasValidPoster: Boolean
    get() = !posterPath.isNullOrBlank()

/**
 * Format rating for UI display
 */
val Movie.formattedRating: String
    get() = String.format("%.1f", voteAverage)


/**
 * Extract release year from date
 */
val Movie.releaseYear: String
    get() = if (releaseDate.length >= 4) {
        releaseDate.substring(0, 4)
    } else {
        "Unknown"
    }

/**
 * Get full poster URL
 */
fun Movie.getPosterUrl(
    baseUrl: String = "https://image.tmdb.org/t/p/",
    size: String = "w342",
): String? {
    return posterPath?.let { "$baseUrl$size$it" }
}

/**
 * Get full backdrop URL
 */
fun Movie.getBackdropUrl(
    baseUrl: String = "https://image.tmdb.org/t/p/",
    size: String = "w780",
): String? {
    return backdropPath?.let { "$baseUrl$size$it" }
}












