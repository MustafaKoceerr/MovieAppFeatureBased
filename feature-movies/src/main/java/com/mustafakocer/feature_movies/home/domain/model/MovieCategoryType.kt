package com.mustafakocer.feature_movies.home.domain.model

enum class MovieCategoryType(val displayName: String, val apiEndpoint: String) {
    NOW_PLAYING("Now Playing", "now_playing"),
    POPULAR("Popular", "popular"),
    TOP_RATED("Top Rated", "top_rated"),
    UPCOMING("Upcoming", "upcoming")
}