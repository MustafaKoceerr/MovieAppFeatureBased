package com.mustafakocer.feature_movies.shared.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import com.mustafakocer.core_database.cache.CacheMetadata

/**
 * A Room Entity designed to store individual movie items for paginated lists.
 *
 * This entity combines the core movie data (similar to `MovieDto`) with additional metadata
 * required for pagination and caching within the local database.
 *
 * @property id The unique identifier for the movie.
 * @property language The language code (e.g., "en-US") for which this movie data was fetched.
 * @property title The movie's title.
 * @property overview A brief summary of the movie.
 * @property posterPath The relative path for the poster image.
 * @property backdropPath The relative path for the backdrop image.
 * @property releaseDate The release date string.
 * @property voteAverage The average user rating.
 * @property voteCount The total number of user votes.
 * @property genreIds A list of genre IDs. Room will handle serialization via a `TypeConverter`.
 * @property adult Indicates if the movie is for an adult audience.
 * @property popularity A metric indicating the movie's popularity.
 * @property originalLanguage The original language of the movie.
 * @property originalTitle The original title of the movie.
 * @property video Indicates if a video trailer is available.
 * @property category The category this movie belongs to (e.g., "popular", "upcoming").
 * @property page The page number from the API where this movie was fetched.
 * @property position The absolute position of the movie in the paginated list, used for stable ordering.
 * @property cacheMetadata Embedded object containing cache-related information like timestamps.
 */
@Entity(
    tableName = "movie_list",
    // Architectural Decision: A composite primary key is used, consisting of the movie `id` and
    // the `language`. This is a critical design choice for a multi-language app. It allows the
    // database to store different versions of the same movie for each supported language,
    // preventing data from one language from overwriting another and ensuring that the cache
    // can serve the correct data based on the user's language preference.
    primaryKeys = ["id", "language"]
)
data class MovieListEntity(
    val id: Int,
    val language: String,

    // Core movie data from MovieDto
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

    // Pagination metadata
    val category: String,
    val page: Int,
    val position: Int,

    // Architectural Decision: The `CacheMetadata` object is embedded directly into this entity.
    // The `@Embedded` annotation is a clean way to include fields from another class as if they
    // were declared directly in this entity. The `prefix` prevents potential column name
    // collisions if other embedded objects were ever added. This composition allows for easy
    // reuse of cache-handling logic.
    @Embedded(prefix = "movie_cache_")
    val cacheMetadata: CacheMetadata,
)