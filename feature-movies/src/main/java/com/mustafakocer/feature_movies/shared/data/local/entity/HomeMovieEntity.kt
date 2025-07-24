package com.mustafakocer.feature_movies.shared.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import com.mustafakocer.core_database.cache.CacheMetadata

/**
 * A Room Entity designed specifically for caching the lists of movies displayed on the home screen.
 *
 * Architectural Decision: This entity was created separately from `MovieListEntity` to decouple the
 * home screen's simple caching needs from the more complex requirements of the paginated movie list screens.
 * The home screen displays non-paginated, fixed-size lists for each category. By using a dedicated
 * entity, we avoid polluting this table with pagination-specific fields (like `page` or `position`),
 * adhering to the Single Responsibility Principle and keeping the data models clean and purpose-focused.
 *
 * @property id The unique identifier for the movie from the API.
 * @property language The language code (e.g., "en-US") for which this movie data was fetched.
 * @property category The home screen category this movie belongs to (e.g., "popular", "upcoming").
 * @property title The movie's title.
 * @property overview A brief summary of the movie.
 * @property posterPath The relative path for the poster image.
 * @property backdropPath The relative path for the backdrop image.
 * @property releaseDate The release date string.
 * @property voteAverage The average user rating.
 * @property voteCount The total number of user votes.
 * @property genreIds A list of genre IDs, handled by a Room `TypeConverter`.
 * @property adult Indicates if the movie is for an adult audience.
 * @property popularity A metric indicating the movie's popularity.
 * @property originalLanguage The original language of the movie.
 * @property originalTitle The original title of the movie.
 * @property video Indicates if a video trailer is available.
 * @property cacheMetadata Embedded object containing cache-related information like timestamps.
 */
@Entity(
    tableName = "home_movies",
    // Architectural Decision: A three-part composite primary key is used. This uniquely identifies
    // a movie entry by its `id`, the `category` it appears in on the home screen, and its `language`.
    // This is necessary because the same movie could potentially appear in multiple categories
    // (e.g., "Popular" and "Now Playing") and must be stored as distinct entries for each context
    // and for each supported language.
    primaryKeys = ["id", "category", "language"]
)
data class HomeMovieEntity(
    val id: Int,
    val language: String,
    val category: String,

    // Core movie data
    val title: String?,
    val overview: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val genreIds: List<Int>?,
    val adult: Boolean?,
    val popularity: Double?,
    val originalLanguage: String?,
    val originalTitle: String?,
    val video: Boolean?,

    // Cache metadata
    @Embedded(prefix = "home_movie_cache_")
    val cacheMetadata: CacheMetadata,
)