package com.mustafakocer.feature_movies.shared.data.mapper

import com.mustafakocer.core_database.cache.CacheDuration
import com.mustafakocer.core_database.cache.CacheMetadata
import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity
import com.mustafakocer.feature_movies.shared.data.model.MovieDto
import kotlinx.serialization.json.Json

// ============================================
// 3. DTO -> ENTITY MAPPERS
// ============================================

// MovieDto -> MovieListEntity
fun MovieDto.toEntity(
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
        cacheMetadata = CacheMetadata.create(CacheDuration.HOURS_24)
    )
}