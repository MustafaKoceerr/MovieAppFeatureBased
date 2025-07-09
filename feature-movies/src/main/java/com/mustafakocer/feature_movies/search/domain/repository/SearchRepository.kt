package com.mustafakocer.feature_movies.search.domain.repository

import androidx.paging.PagingData
import com.mustafakocer.feature_movies.search.domain.model.SearchQuery
import com.mustafakocer.feature_movies.shared.domain.model.MovieList
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
    fun searchMovies(query: SearchQuery): Flow<PagingData<MovieList>>
}