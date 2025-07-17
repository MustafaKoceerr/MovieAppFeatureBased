package com.mustafakocer.feature_movies.shared.data.mapper

import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity
import com.mustafakocer.feature_movies.shared.data.model.MovieDto
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem

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

