package com.mustafakocer.feature_movies.shared.domain.model

/**
 * A rich, detailed domain model designed specifically for the movie details screen.
 *
 * Architectural Decision: This model is intentionally separate from the more lightweight
 * `MovieListItem`. While `MovieListItem` contains just enough data for display in a list,
 * `MovieDetails` includes a comprehensive set of properties required for a dedicated detail view.
 * This separation prevents over-fetching data for list screens and ensures that each part of the
 * application receives a model tailored to its specific needs, adhering to the Single
 * Responsibility Principle.
 *
 * @property id The unique identifier for the movie.
 * @property title The title of the movie.
 * @property overview The full synopsis of the movie.
 * @property posterUrl The relative path for the poster image.
 * @property backdropUrl The relative path for the backdrop (hero) image.
 * @property releaseDate The full release date string (e.g., "2025-07-01").
 * @property voteAverage The raw average rating.
 * @property runtime The movie's runtime in total minutes. The UI layer is responsible for formatting this (e.g., "1h 47m").
 * @property tagline The movie's promotional tagline.
 * @property genres A list of [Genre] objects associated with the movie.
 */
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val releaseDate: String,
    val voteAverage: Double,
    val runtime: Int,
    val tagline: String,
    val genres: List<Genre>
) {

    val hasTagline: Boolean
        get() = tagline.isNotEmpty()
}

/**
 * Represents a single genre as a clean domain model for use within [MovieDetails].
 *
 * @property id The unique identifier for the genre.
 * @property name The name of the genre (e.g., "Action", "Science Fiction").
 */
data class Genre(
    val id: Int,
    val name: String
)