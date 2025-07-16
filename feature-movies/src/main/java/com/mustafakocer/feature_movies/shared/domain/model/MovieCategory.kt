package com.mustafakocer.feature_movies.shared.domain.model

/**
 * Movie category types for API endpoints
 *
 * DESIGN PATTERN: Enum with API mapping
 */

enum class MovieCategory(
    val apiEndpoint: String,
    val cacheKey: String,
) {
    NOW_PLAYING("now_playing", "movies_now_playing"),
    POPULAR("popular", "movies_popular"),
    TOP_RATED("top_rated", "movies_top_rated"),
    UPCOMING("upcoming", "movies_upcoming");

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