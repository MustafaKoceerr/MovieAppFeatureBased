package com.mustafakocer.core_common

/**
 * TEACHING MOMENT: Why Constants in core-common?
 *
 * ✅ Shared across all modules
 * ✅ Single source of truth
 * ✅ Easy to maintain
 * ✅ No duplication
 */

object Constants {
    // API Configuration
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"

    // Image Sizes
    const val POSTER_SIZE = "w342"
    const val BACKDROP_SIZE = "w780"
    const val ORIGINAL_SIZE = "original"

    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val PREFETCH_DISTANCE = 3

    // Timeouts
    const val NETWORK_TIMEOUT = 30_000L
    const val SEARCH_DEBOUNCE = 600L

    // Database
    const val DATABASE_NAME = "movie_database"
    const val DATABASE_VERSION = 1
}