package com.mustafakocer.feature_movies.shared.util

import java.util.Locale

/**
 * A utility object for constructing complete image URLs from the TMDB API.
 *
 * Architectural Decision: This builder object centralizes the logic for creating image URLs.
 * By abstracting the base URL and the specific size paths, it decouples the rest of the application
 * (especially UI components) from the concrete implementation details of the image provider's API.
 * If the image base URL or size specifiers ever change, the update only needs to happen in this one
 * location, making the application more maintainable.
 */
object ImageUrlBuilder {
    private const val BASE_URL = "https://image.tmdb.org/t/p/"

    /**
     * Constructs a full image URL.
     *
     * @param path The relative path of the image file (e.g., "/xyz.jpg").
     * @param size The desired [ImageSize] enum, which specifies the resolution.
     * @return The complete, ready-to-use image URL as a String, or null if the input path is null.
     */
    fun build(path: String?, size: ImageSize): String? {
        return path?.let { "$BASE_URL${size.path}$it" }
    }
}

/**
 * Defines the available image size specifiers as provided by the TMDB API.
 *
 * Using an enum for image sizes ensures type safety and consistency when requesting images,
 * preventing the use of invalid or arbitrary string values throughout the codebase.
 */
enum class ImageSize(val path: String) {
    POSTER_W342("w342"),
    BACKDROP_W780("w780"),
    ORIGINAL("original")
}


val Double.formattedRating: String
    // Using `Locale.getDefault()` ensures that the decimal separator (e.g., "." or ",") is
    // rendered correctly according to the user's device locale settings.
    get() = String.format(Locale.getDefault(), "%.1f", this)