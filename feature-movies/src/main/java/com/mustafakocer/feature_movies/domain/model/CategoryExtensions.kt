package com.mustafakocer.feature_movies.domain.model

// ===== CATEGORY EXTENSIONS =====

/**
 * Get display name for category
 */
val MovieCategory.displayName: String
    get() = when (this) {
        MovieCategory.POPULAR -> "Popular Movies"
        MovieCategory.TOP_RATED -> "Top Rated"
        MovieCategory.NOW_PLAYING -> "Now Playing"
        MovieCategory.UPCOMING -> "Upcoming"
    }