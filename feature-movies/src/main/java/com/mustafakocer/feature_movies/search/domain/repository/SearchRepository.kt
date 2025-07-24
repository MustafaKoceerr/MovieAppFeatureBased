package com.mustafakocer.feature_movies.search.domain.repository

import androidx.paging.PagingData
import com.mustafakocer.feature_movies.search.domain.model.SearchQuery
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for data operations related to the movie search feature.
 *
 * Architectural Decision: This interface serves as a boundary between the domain layer (use cases)
 * and the data layer (repository implementation). It allows the domain layer to request paginated
 * search results without needing to know the specifics of the data source (e.g., a network API)
 * or the implementation details of the Paging 3 library. This abstraction is fundamental to
 * Clean Architecture, promoting modularity and testability.
 */
interface SearchRepository {

    /**
     * Searches for movies that match the given query, providing the results as a paginated stream.
     *
     * The implementation of this function in the data layer will be responsible for setting up
     * the Jetpack Paging 3 `Pager` with a `PagingSource` that fetches search results from the
     * remote API.
     *
     * @param query The [SearchQuery] value object containing the validated search term. Using a
     *              value object instead of a raw String ensures that the repository only receives
     *              queries that have already passed the domain layer's validation rules.
     * @return A [Flow] of [PagingData], which is the core data structure used by the Paging 3
     *         library to represent a stream of paginated content.
     */
    fun searchMovies(query: SearchQuery): Flow<PagingData<MovieListItem>>
}