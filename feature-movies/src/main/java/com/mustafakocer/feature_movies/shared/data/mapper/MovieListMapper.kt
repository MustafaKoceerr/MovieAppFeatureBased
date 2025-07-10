package com.mustafakocer.feature_movies.shared.data.mapper

import com.mustafakocer.feature_movies.list.data.local.entity.MovieListEntity
import com.mustafakocer.feature_movies.shared.data.dto.MovieListDto
import com.mustafakocer.feature_movies.shared.domain.model.MovieList
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * Convert Entity to Domain model
 * Used by Repository to return business objects
 */
fun MovieListEntity.toDomainMovieList(): MovieList {
    return MovieList(
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
fun List<MovieListEntity>.toDomainMovieList(): List<MovieList> {
    return map { it.toDomainMovieList() }
}

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
        genreIds = Json.Default.encodeToString(
            ListSerializer(Int.Companion.serializer()),
            genreIds
        ),
        adult = adult,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        popularity = popularity,
        video = video,
        voteCount = voteCount,
        category = category,
        page = page,
        position = position,
        cacheMetadata = MovieListEntity.Companion.createCacheMetadata(
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

/**
 * Parse genre IDs from JSON string
 * Handles parsing errors gracefully
 */
private fun parseGenreIds(genreIdsJson: String): List<Int> {
    return try {
        Json.Default.decodeFromString(ListSerializer(Int.serializer()), genreIdsJson)
    } catch (e: Exception) {
        emptyList()
    }
}

fun MovieListDto.toDomainMovieList(): MovieList {
    return MovieList(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genreIds = genreIds,
        voteCount = voteCount,
        adult = adult,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        popularity = popularity,
        video = video
    )
}

// Bir listeyi domain modele dönüştürür
fun List<MovieListDto>.toDomainMovies(): List<MovieList> {
    return map { it.toDomainMovieList() }
}