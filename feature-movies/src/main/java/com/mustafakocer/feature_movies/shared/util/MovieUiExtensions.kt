package com.mustafakocer.feature_movies.shared.util

import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import java.util.Locale

// ===== MOVIE UI EXTENSIONS =====
// ÖNERİ: core-android/util/ImageUrlBuilder.kt
object ImageUrlBuilder {
    private const val BASE_URL = "https://image.tmdb.org/t/p/"

    fun build(path: String?, size: ImageSize): String? {
        return path?.let { "$BASE_URL${size.path}$it" }
    }
}

enum class ImageSize(val path: String) {
    POSTER_W342("w342"),
    BACKDROP_W780("w780"),
    ORIGINAL("original")
}

/**
 * Format rating for UI display
 */
val Double.formattedRating: String
    get() = String.format(Locale.getDefault(), "%.1f", this)

