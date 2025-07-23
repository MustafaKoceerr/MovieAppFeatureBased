package com.mustafakocer.feature_movies.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A Data Transfer Object (DTO) representing the detailed movie data from the movie details endpoint.
 *
 * Architectural Decision: This DTO is designed to be a 1-to-1 mapping of the JSON response from the
 * `/movie/{movie_id}` endpoint of the TMDB API. By using a defensive, nullable-by-default approach
 * for all properties that are not guaranteed to be present (i.e., everything except the primary ID),
 * we create a crash-safe deserialization layer. This ensures that even if the API changes or omits
 * certain fields, the application can handle the response gracefully without crashing. The responsibility
 * of handling these nulls and providing defaults is delegated to the mapper function that converts this
 * DTO into a non-nullable domain model (`MovieDetails`).
 *
 * @property id The unique, non-nullable identifier for the movie.
 * @property adult Indicates if the movie is for an adult audience.
 * @property backdropPath The relative path for the backdrop image.
 * @property belongsToCollection Information about the collection this movie belongs to, if any.
 * @property budget The movie's production budget.
 * @property genres A list of genre objects associated with the movie.
 * @property homepage The URL for the movie's official homepage.
 * @property imdbId The movie's ID on IMDb.
 * @property originCountry A list of countries where the movie was produced.
 * @property originalLanguage The original language of the movie.
 * @property originalTitle The original title, often in its native language.
 * @property overview The full synopsis of the movie.
 * @property popularity A metric indicating the movie's popularity.
 * @property posterPath The relative path for the poster image.
 * @property productionCompanies A list of companies that produced the movie.
 * @property productionCountries A list of countries where the movie was produced.
 * @property releaseDate The release date string in "YYYY-MM-DD" format.
 * @property revenue The movie's worldwide revenue.
 * @property runtime The movie's runtime in total minutes.
 * @property spokenLanguages A list of languages spoken in the movie.
 * @property status The release status of the movie (e.g., "Released", "In Production").
 * @property tagline The movie's promotional tagline.
 * @property title The title of the movie.
 * @property video Indicates if a video trailer is available.
 * @property voteAverage The average user rating.
 * @property voteCount The total number of user votes.
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
    val id: Long,
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

/**
 * A DTO representing a single genre.
 */
@Serializable
data class GenreDto(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String? = null,
)

/**
 * A DTO representing a single production company.
 */
@Serializable
data class ProductionCompany(
    @SerialName("id")
    val id: Long,
    @SerialName("logo_path")
    val logoPath: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("origin_country")
    val originCountry: String? = null,
)

/**
 * A DTO representing a single production country.
 */
@Serializable
data class ProductionCountry(
    @SerialName("iso_3166_1")
    val iso31661: String? = null,
    @SerialName("name")
    val name: String? = null,
)

/**
 * A DTO representing a single spoken language.
 */
@Serializable
data class SpokenLanguage(
    @SerialName("english_name")
    val englishName: String? = null,
    @SerialName("iso_639_1")
    val iso6391: String? = null,
    @SerialName("name")
    val name: String? = null,
)

/**
 * A DTO representing the collection a movie belongs to.
 */
@Serializable
data class BelongsToCollection(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
)