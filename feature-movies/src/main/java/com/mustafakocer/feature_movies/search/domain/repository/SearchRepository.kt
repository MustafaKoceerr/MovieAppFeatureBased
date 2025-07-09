package com.mustafakocer.feature_movies.search.domain.repository

import androidx.paging.PagingData
import com.mustafakocer.feature_movies.search.domain.model.SearchQuery
import com.mustafakocer.feature_movies.shared.domain.model.Movie
import kotlinx.coroutines.flow.Flow

/**
 * Search repository contract
 *
 * CLEAN ARCHITECTURE: Domain Layer - Repository Abstraction
 * RESPONSIBILITY: Define data operations for search functionality
 */
interface SearchRepository {

    /**
     * Search movies with pagination
     *
     * @param query Search query object
     * @return Flow of paged search results
     */
    fun searchMovies(query: SearchQuery): Flow<PagingData<Movie>>

    /**
     * Clear search cache
     */
    suspend fun clearSearchCache()

    /**
     * Check şf we have cached search result for the query
     */
    suspend fun hasCachedResults(query: SearchQuery): Boolean
}