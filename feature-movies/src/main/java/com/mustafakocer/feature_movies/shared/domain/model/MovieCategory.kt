package com.mustafakocer.feature_movies.shared.domain.model

/**
 * Movie category types for API endpoints
 *
 * DESIGN PATTERN: Enum with API mapping
 */

enum class MovieCategory(
    val apiEndpoint: String,
    val title: String,
    val cacheKey: String
) {
    NOW_PLAYING("now_playing", "Now Playing", "movies_now_playing"),
    POPULAR("popular", "Popular", "movies_popular"),
    TOP_RATED("top_rated", "Top Rated", "movies_top_rated"),
    UPCOMING("upcoming", "Upcoming", "movies_upcoming");

    companion object {
        /**
         * Convert API endpoint string to MovieCategory
         * Useful for navigation parameter mapping
         */
        fun fromApiEndpoint(endpoint: String): MovieCategory? {
            return entries.find { it.apiEndpoint == endpoint }
        }

        /**
         * Get all available categories
         * Useful for UI iteration
         */
        fun getAllCategories(): List<MovieCategory> {
            return entries
        }
    }
}