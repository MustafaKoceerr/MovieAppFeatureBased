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
import com.mustafakocer.feature_movies.shared.util.ImageSize
import com.mustafakocer.feature_movies.shared.util.ImageUrlBuilder

// ============================================
// 1. ENTITY -> DOMAIN MAPPERS
// ============================================

// MovieListEntity -> MovieListItem (Domain)
fun MovieListEntity.toDomainList(): MovieListItem {
    return MovieListItem(
        id = id,
        title = title?.takeIf { it.isNotBlank() } ?: "Unknown Title",
        overview = overview?.takeIf { it.isNotBlank() } ?: "No overview available",
        posterUrl = ImageUrlBuilder.build(posterPath, ImageSize.POSTER_W342),
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



// HomeMovieEntity -> MovieListItem (Domain)
fun HomeMovieEntity.toDomainList(): MovieListItem {
    return MovieListItem(
        id = id,
        title = title?.takeIf { it.isNotBlank() } ?: "Unknown Title",
        overview = overview?.takeIf { it.isNotBlank() } ?: "No overview available",
        posterUrl = ImageUrlBuilder.build(posterPath, ImageSize.POSTER_W342),
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

// MovieDto -> MovieListItem (Domain)
fun MovieDto.toDomainList(): MovieListItem {
    return MovieListItem(
        id = id,
        title = title?.takeIf { it.isNotBlank() } ?: "Unknown Title",
        overview = overview?.takeIf { it.isNotBlank() } ?: "No overview available",
        posterUrl = ImageUrlBuilder.build(posterPath, ImageSize.POSTER_W342),
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


// MovieDetailsDto -> MovieDetails (Domain)
fun MovieDetailsDto.toDomain(): MovieDetails {
    return MovieDetails(
        id = id.toInt(),
        title = title?.takeIf { it.isNotBlank() } ?: "",
        overview = overview?.takeIf { it.isNotBlank() } ?: "",
        posterUrl = ImageUrlBuilder.build(posterPath, ImageSize.POSTER_W342) ?: "",
        backdropUrl = ImageUrlBuilder.build(backdropPath, ImageSize.BACKDROP_W780) ?: "",
        releaseDate = releaseDate?.takeIf { it.isNotBlank() } ?: "",
        voteAverage = voteAverage ?: 0.0,
        runtime = runtime?.toInt() ?: 0,
        tagline = tagline?.takeIf { it.isNotBlank() } ?: "",
        genres = genres?.mapNotNull { genreDto ->
            genreDto.name?.takeIf { it.isNotBlank() }?.let { name ->
                Genre(id = genreDto.id.toInt(), name = name)
            }
        } ?: listOf()
    )
}



// ============================================
// 3. DTO -> ENTITY MAPPERS
// ============================================

// MovieDto -> MovieListEntity
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
        genreIds = genreIds, // 🚀 Direkt assign! TypeConverter otomatik handle eder
        adult = adult,
        popularity = popularity,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        video = video,
        // Remote mediator tarafından parametre olarak gönderilecekler.
        category = category,
        page = page,
        position = position,
        language = language,
        cacheMetadata = CacheMetadata.create(CacheDuration.TWENTY_FOUR_HOURS_MS)
    )
}

// MovieDto -> HomeMovieEntity
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

// List<MovieListEntity> -> List<MovieListItem>
@JvmName("toDomainListFromEntity")
fun List<MovieListEntity>.toDomainList(): List<MovieListItem> {
    return this.map { it.toDomainList() }
}

// List<MovieDto> -> List<MovieListItem>
@JvmName("toDomainListFromDto")
fun List<MovieDto>.toDomainList(): List<MovieListItem> {
    return this.map { it.toDomainList() }
}

// toEntityList() fonksiyonu
fun List<MovieDto>.toEntityList(
    category: String,
    page: Int,
    pageSize: Int,
    language: String,
): List<MovieListEntity> {
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

// List<HomeMovieEntity> -> List<MovieListItem>
@JvmName("toDomainListFromHomeEntity")
fun List<HomeMovieEntity>.toDomainList(): List<MovieListItem> {
    return this.map { it.toDomainList() }
}

// List<MovieDto> -> List<HomeMovieEntity>
fun List<MovieDto>.toHomeMovieEntityList(
    category: String,
    language: String,
): List<HomeMovieEntity> {
    return this.map { it.toHomeMovieEntity(category, language) }
}