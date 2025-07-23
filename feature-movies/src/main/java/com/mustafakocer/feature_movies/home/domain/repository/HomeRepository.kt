package com.mustafakocer.feature_movies.home.domain.repository

import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for data operations related to the home screen feature.
 *
 * Architectural Decision: This interface acts as a boundary between the domain layer (use cases)
 * and the data layer (repository implementation). It allows the domain layer to request data
 * without needing to know the specifics of where the data comes from (e.g., network API, local
 * database, or a combination of both). This abstraction is a core principle of Clean Architecture,
 * promoting modularity, testability, and maintainability.
 */
interface HomeRepository {

    /**
     * Fetches a list of movies for a specific category.
     *
     * The implementation of this function in the data layer will be responsible for handling
     * the actual data fetching logic, including network requests, caching strategies, and error handling.
     *
     * @param category The [MovieCategory] for which to fetch the movies.
     * @param isRefresh A flag to indicate whether the data should be forcibly refreshed from the
     *                  primary data source (e.g., network), bypassing any local cache. This is
     *                  typically used for user-driven actions like pull-to-refresh.
     * @return A [Flow] that emits [Resource] states. This allows the caller (use case) to reactively
     *         observe the loading, success (with a list of [MovieListItem]), and error states of the
     *         data fetching operation.
     */
    fun getMoviesForCategory(
        category: MovieCategory,
        isRefresh: Boolean = false
    ): Flow<Resource<List<MovieListItem>>>
}