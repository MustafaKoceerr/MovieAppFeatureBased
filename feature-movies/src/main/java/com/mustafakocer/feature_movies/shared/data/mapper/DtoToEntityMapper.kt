package com.mustafakocer.feature_movies.shared.data.mapper

import com.mustafakocer.core_database.cache.CacheDuration
import com.mustafakocer.core_database.cache.CacheMetadata
import com.mustafakocer.feature_movies.shared.data.local.entity.HomeMovieEntity
import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity
import com.mustafakocer.feature_movies.shared.data.model.MovieDto

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
        cacheMetadata = CacheMetadata.create(CacheDuration.HOURS_24)
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
        cacheMetadata = CacheMetadata.create(CacheDuration.HOURS_24)
    )
}