package com.mustafakocer.feature_movies.domain.model

/**
 * Movie categories for different API endpoints
 */
enum class MovieCategory(val endpoint: String) {
    POPULAR("popular"),
    TOP_RATED("top_rated"),
    NOW_PLAYING("now_playing"),
    UPCOMING("upcoming")
}