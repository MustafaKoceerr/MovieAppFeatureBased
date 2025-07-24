package com.mustafakocer.feature_movies.list.domain.repository

import androidx.paging.PagingData
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for data operations related to the paginated movie list feature.
 *
 * Architectural Decision: This interface serves as a boundary between the domain layer (use cases)
 * and the data layer (repository implementation). It allows the domain layer to request paginated
 * data and trigger refreshes without being aware of the underlying data sources (network, database)
 * or the implementation details of the Paging 3 library.
 */
interface MovieListRepository {

    /**
     * Retrieves a stream of paginated movie data for a specific category and language.
     *
     * The implementation of this function in the data layer will be responsible for setting up
     * the Jetpack Paging 3 `Pager` with a `PagingSource` that fetches data from the network.
     *
     * @param category The [MovieCategory] for which to fetch the movies.
     * @param language The language code (e.g., "en-US") for the API request.
     * @return A [Flow] of [PagingData], which is the core data structure used by the Paging 3
     *         library to represent a stream of paginated content.
     */
    fun getMoviesByCategory(
        category: MovieCategory,
        language: String,
    ): Flow<PagingData<MovieListItem>>

    /**
     * Clears the local cache for a specific category and language.
     *
     * @param category The [MovieCategory] whose cache should be cleared.
     * @param language The language for which the cache should be cleared.
     */
    suspend fun refreshCategory(
        category: MovieCategory,
        language: String,
    )
}