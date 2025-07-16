package com.mustafakocer.feature_movies.shared.domain.model


data class MovieListItem(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?, // Tam URL, örn: "https://image.tmdb.org/t/p/w500/..."
    val releaseYear: String, // Sadece yıl, örn: "2025"
    val voteAverage: Double, // Ham veri, örn: 8.1
    val voteCount: Int
)
