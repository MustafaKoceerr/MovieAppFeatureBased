package com.mustafakocer.feature_movies.shared.domain.model

/**
 * Sadece film detay ekranında kullanılmak üzere tasarlanmış,
 * zengin ve detaylı domain modeli.
 */
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val releaseDate: String, // Tam tarih, örn: "2025-07-01"
    val voteAverage: Double,
    // UI'da doğrudan gösterilecek, formatlanmış veri.
    val runtime: Int, // Örn: "1h 47m"
    val tagline: String,
    val genres: List<Genre>
) {
    val hasTagline: Boolean
        get() = !tagline.isEmpty()
}

/**
 * MovieDetails içinde kullanılacak olan, temiz Genre modeli.
 */
data class Genre(
    val id: Int,
    val name: String
)