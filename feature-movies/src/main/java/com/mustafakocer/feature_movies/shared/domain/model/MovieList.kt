package com.mustafakocer.feature_movies.shared.domain.model

/**
 * Pagination'da kullanılacak data model.
 */
data class MovieList(
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
    val originalLanguage: String,     // ✅ Yeni eklendi
    val originalTitle: String,        // ✅ Yeni eklendi
    val popularity: Double,           // ✅ Yeni eklendi
    val video: Boolean,                // ✅ Yeni eklendi
)