package com.mustafakocer.database_contracts.entities

/**
 * Movie entity contract
 *
 * CLEAN ARCHITECTURE: Domain/Infrastructure boundary
 * Responsibility: Define movie data structure contract
 */
interface MovieEntityContract : CacheAwareEntityContract, PageableEntityContract{
    override val id: Int // Bu satırı ekle
    val title: String
    val overview: String
    val posterPath: String?
    val backdropPath: String?
    val releaseDate: String
    val voteAverage: Double
    val genreIds: String // JSON string
    val adult: Boolean
    val originalLanguage: String
    val originalTitle: String
    val popularity: Double
    val video: Boolean
    val voteCount: Int
}