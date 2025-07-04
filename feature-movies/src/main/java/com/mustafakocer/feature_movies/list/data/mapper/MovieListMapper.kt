package com.mustafakocer.feature_movies.list.data.mapper

import com.mustafakocer.feature_movies.list.data.local.entity.MovieListEntity
import com.mustafakocer.feature_movies.list.data.remote.dto.MovieListDto
import com.mustafakocer.feature_movies.list.domain.model.MovieListItem
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * Movie list mappers
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer
 * RESPONSIBILITY: Convert between layers (DTO ↔ Entity ↔ Domain) */

// ==================== DOMAIN MAPPING ====================

/**
 * Convert Entity to Domain model
 * Used by Repository to return business objects
 */
fun MovieListEntity.toDomain(): MovieListItem {
    return MovieListItem(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genreIds = parseGenreIds(genreIds),
        adult = adult,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        popularity = popularity,
        video = video,
        voteCount = voteCount
    )
}

/**
 * Convert list of entities to domain models
 */
fun List<MovieListEntity>.toDomain(): List<MovieListItem> {
    return map { it.toDomain() }
}

// ==================== ENTITY MAPPING ====================

/**
 * Convert DTO to Entity
 * Used by RemoteMediator to save API data
 */
fun MovieListDto.toEntity(
    category: String,
    page: Int,
    position: Int,
): MovieListEntity {
    return MovieListEntity(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genreIds = Json.encodeToString(ListSerializer(Int.serializer()), genreIds),
        adult = adult,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        popularity = popularity,
        video = video,
        voteCount = voteCount,
        category = category,
        page = page,
        position = position,
        cacheMetadata = MovieListEntity.createCacheMetadata(
            category = category,
            page = page,
            cacheTimeoutHours = 24
        )
    )
}


/**
 * Convert list of DTOs to entities
 */
fun List<MovieListDto>.toEntity(
    category: String,
    page: Int,
): List<MovieListEntity> {
    return mapIndexed { index, dto ->
        dto.toEntity(
            category = category,
            page = page,
            position = index
        )
    }
}

// ==================== HELPER FUNCTIONS ====================

/**
 * Parse genre IDs from JSON string
 * Handles parsing errors gracefully
 */
private fun parseGenreIds(genreIdsJson: String): List<Int> {
    return try {
        Json.decodeFromString(ListSerializer(Int.serializer()), genreIdsJson)
    } catch (e: Exception) {
        emptyList()
    }
}