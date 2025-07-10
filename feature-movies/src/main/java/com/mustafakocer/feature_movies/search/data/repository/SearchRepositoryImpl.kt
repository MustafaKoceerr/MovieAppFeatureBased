package com.mustafakocer.feature_movies.search.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mustafakocer.core_database.pagination.PaginationSettings
import com.mustafakocer.feature_movies.search.data.paging.SearchPagingSource
import com.mustafakocer.feature_movies.search.domain.model.SearchQuery
import com.mustafakocer.feature_movies.search.domain.repository.SearchRepository
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.domain.model.Movie
import com.mustafakocer.feature_movies.shared.domain.model.MovieList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Search repository implementation - KISS principle
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Repository Implementation
 * RESPONSIBILITY: Coordinate search data sources
 *
 * DESIGN CHOICE: Direct API pagination without database
 * - Search results are temporary and don't need caching
 * - Simpler implementation without complex cache management
 * - Good performance for search use case
 */

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService,
    @Named("tmdb_api_key") private val apiKey: String,
    ): SearchRepository{

    override fun searchMovies(query: SearchQuery): Flow<PagingData<MovieList>> {
        return Pager(
            config = PaginationSettings.search.toPagingConfig(),
            pagingSourceFactory = {
                SearchPagingSource(
                    movieApiService = movieApiService,
                    apiKey = apiKey,
                    searchQuery = query.cleanQuery
                )
            }
        ).flow
    }

}