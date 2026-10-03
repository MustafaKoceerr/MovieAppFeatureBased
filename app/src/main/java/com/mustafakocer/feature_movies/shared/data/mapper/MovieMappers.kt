package com.mustafakocer.feature_movies.shared.data.mapper

import com.mustafakocer.core_database.cache.CacheDuration
import com.mustafakocer.core_database.cache.CacheMetadata
import com.mustafakocer.feature_movies.shared.data.local.entity.HomeMovieEntity
import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity
import com.mustafakocer.feature_movies.shared.data.model.MovieDetailsDto
import com.mustafakocer.feature_movies.shared.data.model.MovieDto
import com.mustafakocer.feature_movies.shared.domain.model.Genre
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem

/**
 * This file contains extension functions for mapping between different data layers of the application,
 * following the principles of Clean Architecture.
 *
 * - **DTO (Data Transfer Object):** Models that directly represent the JSON structure of the remote API.
 * - **Entity:** Models that represent the schema of the local database (Room).
 * - **Domain Model:** Clean, plain Kotlin data classes that represent the core business objects of the
 *   application, used by the UI and domain layers.
 *
 * These mappers act as an "anti-corruption layer," protecting the domain and UI from changes in the
 * data sources and ensuring that each layer works with a model tailored to its specific needs.
 */

// ============================================
// 1. ENTITY -> DOMAIN MAPPERS
// ============================================

/**
 * Maps a [MovieListEntity] from the database to a [MovieListItem] domain model.
 *
 * This function is responsible for converting cached data into a format suitable for presentation
 * in the UI, applying default values for any potentially missing data to ensure robustness.
 *
 * @return A [MovieListItem] domain model.
 */
fun MovieListEntity.toDomainList(): MovieListItem {
    return MovieListItem(
        id = id,
        // Defensive Programming: Provides a default title if the cached title is null or blank.
        title = title?.takeIf { it.isNotBlank() } ?: "Unknown Title",
        overview = overview?.takeIf { it.isNotBlank() } ?: "No overview available",
        // The poster path is converted to a full, usable URL by the builder.
        posterUrl = posterPath,
        // Safely extracts the year from the release date string, with a fallback for invalid formats.
        releaseYear = releaseDate?.takeIf { it.isNotBlank() }?.let { date ->
            try {
                date.substring(0, 4)
            } catch (e: Exception) {
                "Unknown"
            }
        } ?: "Unknown",
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0
    )
}

/**
 * Maps a [HomeMovieEntity] from the database to a [MovieListItem] domain model.
 *
 * @return A [MovieListItem] domain model.
 */
fun HomeMovieEntity.toDomainList(): MovieListItem {
    return MovieListItem(
        id = id,
        title = title?.takeIf { it.isNotBlank() } ?: "Unknown Title",
        overview = overview?.takeIf { it.isNotBlank() } ?: "No overview available",
        posterUrl = posterPath,
        releaseYear = releaseDate?.takeIf { it.isNotBlank() }?.let { date ->
            try {
                date.substring(0, 4)
            } catch (e: Exception) {
                "Unknown"
            }
        } ?: "Unknown",
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0
    )
}

// ============================================
// 2. DTO -> DOMAIN MAPPERS
// ============================================

/**
 * Maps a [MovieDto] from the network to a [MovieListItem] domain model.
 *
 * This function serves as a crucial part of the anti-corruption layer, translating potentially
 * unreliable network data into a clean, non-nullable domain model for the rest of the app.
 *
 * @return A [MovieListItem] domain model.
 */
fun MovieDto.toDomainList(): MovieListItem {
    return MovieListItem(
        id = id,
        title = title?.takeIf { it.isNotBlank() } ?: "Unknown Title",
        overview = overview?.takeIf { it.isNotBlank() } ?: "No overview available",
        posterUrl = posterPath,
        releaseYear = releaseDate?.takeIf { it.isNotBlank() }?.let { date ->
            try {
                date.substring(0, 4)
            } catch (e: Exception) {
                "Unknown"
            }
        } ?: "Unknown",
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0
    )
}

/**
 * Maps a [MovieDetailsDto] from the network to a rich [MovieDetails] domain model.
 *
 * @return A [MovieDetails] domain model.
 */
fun MovieDetailsDto.toDomain(): MovieDetails {
    return MovieDetails(
        id = id.toInt(),
        title = title?.takeIf { it.isNotBlank() } ?: "",
        overview = overview?.takeIf { it.isNotBlank() } ?: "",
        posterUrl = posterPath ?: "",
        backdropUrl = backdropPath ?: "",
        releaseDate = releaseDate?.takeIf { it.isNotBlank() } ?: "",
        voteAverage = voteAverage ?: 0.0,
        runtime = runtime?.toInt() ?: 0,
        tagline = tagline?.takeIf { it.isNotBlank() } ?: "",
        // Architectural Decision: `mapNotNull` provides a robust way to parse the list of genres.
        // It ensures that any genre from the DTO with a null or blank name is safely discarded,
        // preventing nulls or empty items in the final domain model's genre list.
        genres = genres?.mapNotNull { genreDto ->
            genreDto.name?.takeIf { it.isNotBlank() }?.let { name ->
                Genre(id = genreDto.id.toInt(), name = name)
            }
        } ?: emptyList()
    )
}

// ============================================
// 3. DTO -> ENTITY MAPPERS
// ============================================

/**
 * Maps a [MovieDto] from the network to a [MovieListEntity] for database insertion.
 *
 * This function enriches the network data with additional metadata required for local caching,
 * such as category, page, position, language, and cache timestamps.
 *
 * @param category The category this movie belongs to (e.g., "popular").
 * @param page The page number this movie was fetched from.
 * @param position The absolute position of this movie in the paginated list.
 * @param language The language this movie was fetched for.
 * @return A [MovieListEntity] ready for database insertion.
 */
fun MovieDto.toEntity(
    category: String,
    page: Int,
    position: Int,
    language: String,
): MovieListEntity {
    return MovieListEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        // Architectural Decision: The `genreIds` list is assigned directly. Room handles the
        // conversion to a storable format (like a JSON string) automatically via a TypeConverter,
        // simplifying the mapping logic.
        genreIds = genreIds,
        adult = adult,
        popularity = popularity,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        video = video,
        // Metadata for caching and pagination.
        category = category,
        page = page,
        position = position,
        language = language,
        // Stamp the entity with cache metadata upon creation.
        cacheMetadata = CacheMetadata.create(CacheDuration.TWENTY_FOUR_HOURS_MS)
    )
}

/**
 * Maps a [MovieDto] from the network to a [HomeMovieEntity] for the home screen cache.
 *
 * @param category The category this movie belongs to.
 * @param language The language this movie was fetched for.
 * @return A [HomeMovieEntity] ready for database insertion.
 */
fun MovieDto.toHomeMovieEntity(
    category: String,
    language: String,
): HomeMovieEntity {
    return HomeMovieEntity(
        id = id,
        language = language,
        category = category,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genreIds = genreIds,
        adult = adult,
        popularity = popularity,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        video = video,
        cacheMetadata = CacheMetadata.create(CacheDuration.TWENTY_FOUR_HOURS_MS)
    )
}

// ============================================
// 4. LIST EXTENSION FUNCTIONS
// ============================================

/**
 * Maps a list of [MovieListEntity] to a list of [MovieListItem] domain models.
 */
@JvmName("toDomainListFromEntity")
fun List<MovieListEntity>.toDomainList(): List<MovieListItem> {
    return this.map { it.toDomainList() }
}

/**
 * Maps a list of [MovieDto] to a list of [MovieListItem] domain models.
 */
@JvmName("toDomainListFromDto")
fun List<MovieDto>.toDomainList(): List<MovieListItem> {
    return this.map { it.toDomainList() }
}

/**
 * Maps a list of [MovieDto] to a list of [MovieListEntity], calculating the absolute position
 * for each item, which is essential for stable pagination.
 *
 * @param category The category these movies belong to.
 * @param page The page number these movies were fetched from.
 * @param pageSize The number of items per page, used to calculate the starting position.
 * @param language The language these movies were fetched for.
 * @return A list of [MovieListEntity] ready for database insertion.
 */
fun List<MovieDto>.toEntityList(
    category: String,
    page: Int,
    pageSize: Int,
    language: String,
): List<MovieListEntity> {
    // Calculate the starting position for items on this page to ensure a continuous, ordered list.
    val startPosition = (page - 1) * pageSize

    return this.mapIndexed { index, movieDto ->
        movieDto.toEntity(
            language = language,
            category = category,
            page = page,
            position = startPosition + index
        )
    }
}

/**
 * Maps a list of [HomeMovieEntity] to a list of [MovieListItem] domain models.
 */
@JvmName("toDomainListFromHomeEntity")
fun List<HomeMovieEntity>.toDomainList(): List<MovieListItem> {
    return this.map { it.toDomainList() }
}

/**
 * Maps a list of [MovieDto] to a list of [HomeMovieEntity].
 */
fun List<MovieDto>.toHomeMovieEntityList(
    category: String,
    language: String,
): List<HomeMovieEntity> {
    return this.map { it.toHomeMovieEntity(category, language) }
}