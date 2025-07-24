package com.mustafakocer.feature_movies.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single movie item as a Data Transfer Object (DTO) from the remote API.
 *
 * This class is designed to directly map the JSON structure of a movie object returned by list-based
 * endpoints from the TMDB API (e.g., popular, search).
 *
 * @property id The unique TMDB identifier for the movie.
 * @property title The title of the movie.
 * @property overview A brief summary of the movie.
 * @property posterPath The relative path for the poster image (e.g., "/path.jpg").
 * @property backdropPath The relative path for the backdrop image.
 * @property releaseDate The release date string in "YYYY-MM-DD" format.
 * @property voteAverage The average user rating.
 * @property voteCount The total number of user votes.
 * @property genreIds A list of IDs corresponding to the movie's genres.
 * @property adult Indicates if the movie is for an adult audience.
 * @property popularity A metric indicating the movie's popularity.
 * @property originalLanguage The original language of the movie (e.g., "en").
 * @property originalTitle The original title, often in its native language.
 * @property video Indicates if a video trailer is available.
 */
@Serializable
data class MovieDto(

    @SerialName("id")
    val id: Int,

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