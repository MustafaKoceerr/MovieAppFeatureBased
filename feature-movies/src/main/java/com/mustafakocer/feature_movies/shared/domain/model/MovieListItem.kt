package com.mustafakocer.feature_movies.shared.domain.model

/**
 * Represents a single movie item as a clean domain model.
 *
 * Architectural Decision: This data class is a core part of the domain layer. It serves as a
 * "clean" representation of a movie, containing only the data necessary for the UI and business
 * logic. It is completely decoupled from the data layer's specific DTOs (Data Transfer Objects)
 * from the network API. This separation, achieved through a mapping process, ensures that changes
 * in the API's data structure do not directly impact the UI or domain layers, which is a fundamental
 * principle of Clean Architecture.
 *
 * @property id The unique identifier for the movie.
 * @property title The title of the movie.
 * @property overview A brief summary or synopsis of the movie.
 * @property posterUrl The relative path of the poster image (e.g., "/path/to/poster.jpg").
 *                     This path is intended to be used with the `ImageUrlBuilder`.
 * @property releaseYear The year the movie was released (e.g., "2023").
 * @property voteAverage The raw, unformatted average rating (e.g., 8.1).
 * @property voteCount The total number of votes the rating is based on.
 */
data class MovieListItem(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?,
    val releaseYear: String,
    val voteAverage: Double,
    val voteCount: Int
)