package com.mustafakocer.feature_movies.shared.domain.model

/**
 * Represents the different categories of movies available from the API.
 *
 * Architectural Decision: This enum acts as a type-safe mapping between a user-facing concept
 * (e.g., 'Popular Movies') and the specific technical strings required by the backend API
 * (`apiEndpoint`) and the local cache (`cacheKey`). This prevents the use of raw "magic strings"
 * throughout the codebase, reducing the risk of typos and making the code more maintainable and
 * readable. It encapsulates all related values for a category in one place.
 *
 * @property apiEndpoint The string value used to query the specific movie category from the remote API.
 * @property cacheKey A unique key used to identify this category's data in the local cache (e.g.,
 *                    in the Room database or for RemoteKeys). This ensures that data for different
 *                    categories does not conflict.
 */
enum class MovieCategory(
    val apiEndpoint: String,
    val cacheKey: String,
) {
    NOW_PLAYING("now_playing", "movies_now_playing"),
    POPULAR("popular", "movies_popular"),
    TOP_RATED("top_rated", "movies_top_rated"),
    UPCOMING("upcoming", "movies_upcoming");

    /**
     * Provides utility functions for working with the `MovieCategory` enum.
     */
    companion object {
        /**
         * A safe factory method to convert a raw string (typically from a navigation argument)
         * into a `MovieCategory` instance.
         *
         * @param endpoint The string representation of the API endpoint.
         * @return The matching [MovieCategory], or `null` if no match is found.
         */
        fun fromApiEndpoint(endpoint: String): MovieCategory? {
            return entries.find { it.apiEndpoint == endpoint }
        }

        /**
         * Provides a complete list of all defined movie categories.
         *
         * @return A list containing all instances of [MovieCategory].
         */
        fun getAllCategories(): List<MovieCategory> {
            return entries
        }
    }
}