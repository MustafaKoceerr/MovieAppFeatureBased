package com.mustafakocer.feature_movies.shared.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val genreIds: List<Int>,
    val voteCount: Int = 0,           // ✅ UI'da kullanılıyor
    val adult: Boolean = false,       // ✅ Filtreleme için yararlı
)
