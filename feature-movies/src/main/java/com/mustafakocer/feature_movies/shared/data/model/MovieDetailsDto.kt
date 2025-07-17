package com.mustafakocer.feature_movies.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Detay endpoint'inden gelen film verisini temsil eden DTO.
 * ID'ler hariç tüm alanlar, API'dan null gelebileceği ihtimaline karşı nullable olarak tanımlanmıştır.
 * Defensive programming yaklaşımı ile crash-safe tasarım.
 */

@Serializable
data class MovieDetailsDto(
    @SerialName("adult")
    val adult: Boolean? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("belongs_to_collection")
    val belongsToCollection: BelongsToCollection? = null,
    @SerialName("budget")
    val budget: Long? = null,
    @SerialName("genres")
    val genres: List<GenreDto>? = null,
    @SerialName("homepage")
    val homepage: String? = null,
    @SerialName("id")
    val id: Long, // ID zorunlu - primary key
    @SerialName("imdb_id")
    val imdbId: String? = null,
    @SerialName("origin_country")
    val originCountry: List<String>? = null,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    @SerialName("original_title")
    val originalTitle: String? = null,
    @SerialName("overview")
    val overview: String? = null,
    @SerialName("popularity")
    val popularity: Double? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("production_companies")
    val productionCompanies: List<ProductionCompany>? = null,
    @SerialName("production_countries")
    val productionCountries: List<ProductionCountry>? = null,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("revenue")
    val revenue: Long? = null,
    @SerialName("runtime")
    val runtime: Long? = null,
    @SerialName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage>? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("tagline")
    val tagline: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("video")
    val video: Boolean? = null,
    @SerialName("vote_average")
    val voteAverage: Double? = null,
    @SerialName("vote_count")
    val voteCount: Long? = null,
)

@Serializable
data class GenreDto(
    @SerialName("id")
    val id: Long, // ID zorunlu
    @SerialName("name")
    val name: String? = null,
)

@Serializable
data class ProductionCompany(
    @SerialName("id")
    val id: Long, // ID zorunlu
    @SerialName("logo_path")
    val logoPath: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("origin_country")
    val originCountry: String? = null,
)

@Serializable
data class ProductionCountry(
    @SerialName("iso_3166_1")
    val iso31661: String? = null,
    @SerialName("name")
    val name: String? = null,
)

@Serializable
data class SpokenLanguage(
    @SerialName("english_name")
    val englishName: String? = null,
    @SerialName("iso_639_1")
    val iso6391: String? = null,
    @SerialName("name")
    val name: String? = null,
)

@Serializable
data class BelongsToCollection(
    @SerialName("id")
    val id: Long, // ID zorunlu
    @SerialName("name")
    val name: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
)