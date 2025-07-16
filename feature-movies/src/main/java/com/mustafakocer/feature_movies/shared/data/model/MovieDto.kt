package com.mustafakocer.feature_movies.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Liste ve arama endpoint'lerinden gelen tek bir film öğesini temsil eden DTO.
 * Tüm alanlar, API'dan null gelebileceği ihtimaline karşı nullable olarak tanımlanmıştır.
 */
@Serializable
data class MovieDto(
    @SerialName("id")
    val id: Int, // ID genellikle zorunludur, ama güvende olmak için nullable yapılabilir.

    @SerialName("title")
    val title: String?,

    @SerialName("overview")
    val overview: String?,

    @SerialName("poster_path")
    val posterPath: String?,

    @SerialName("backdrop_path")
    val backdropPath: String?,

    @SerialName("release_date")
    val releaseDate: String?,

    @SerialName("vote_average")
    val voteAverage: Double?,

    @SerialName("vote_count")
    val voteCount: Int?,

    @SerialName("genre_ids")
    val genreIds: List<Int>?,

    @SerialName("adult")
    val adult: Boolean?,

    @SerialName("popularity")
    val popularity: Double?,

    @SerialName("original_language")
    val originalLanguage: String?,

    @SerialName("original_title")
    val originalTitle: String?,

    @SerialName("video")
    val video: Boolean?
)