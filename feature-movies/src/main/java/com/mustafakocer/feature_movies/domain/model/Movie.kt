package com.mustafakocer.feature_movies.domain.model

/**
 * TEACHING MOMENT: Domain Models
 *
 * DOMAIN LAYER'DA:
 * ✅ Business logic representation
 * ✅ UI'dan bağımsız
 * ✅ API'den bağımsız
 * ✅ Pure Kotlin data classes
 * ✅ Business rules encapsulation
 */

/**
 * Core movie domain model
 * Bu model UI'da gösterilecek movie bilgilerini temsil eder
 */
/**
 * TEACHING MOMENT: Minimal Domain Models
 *
 * DOMAIN LAYER BEST PRACTICES:
 * ✅ Sadece core business data
 * ✅ Gereksiz computed property yok
 * ✅ UI formatting yok (extensions'da)
 * ✅ API pollution yok
 */

/**
 * Core movie domain model
 * Sadece essential movie bilgileri
 */
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val genreIds: List<Int>
)