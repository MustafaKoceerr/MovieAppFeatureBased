package com.mustafakocer.feature_movies.list.domain.repository

import androidx.paging.PagingData
import com.mustafakocer.feature_movies.list.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieList
import kotlinx.coroutines.flow.Flow

/**
 * Movie list repository contract
 *
 * CLEAN ARCHITECTURE: Domain Layer - Repository Contract
 * RESPONSIBILITY: Define business operations for movie lists
 * */
interface MovieListRepository {

    /**
     * Get paginated movies for specific category
     */
    fun getMoviesForCategory(category: MovieCategory): Flow<PagingData<MovieList>>

    /**
     * Refresh cache for specific category
     */
    suspend fun refreshCategory(category: MovieCategory)

    /**
     * Clear cache for specific category
     */
    suspend fun clearCacheForCategory(category: MovieCategory)

    /**
     * Check if category has cached data
     */
    suspend fun hasCachedData(category: MovieCategory): Boolean
}