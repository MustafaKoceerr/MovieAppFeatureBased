package com.mustafakocer.feature_movies.list.domain.usecase

import com.mustafakocer.feature_movies.list.domain.repository.MovieListRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import javax.inject.Inject

/**
 * A use case dedicated to handling the business logic for forcing a refresh of a movie list.
 *
 * Architectural Decision: While data fetching is handled by `GetMovieListUseCase`, this separate
 * use case exists to represent a very specific, explicit user action: invalidating the cache
 * for a category. This is typically triggered by a "pull-to-refresh" gesture. Separating this
 * into its own use case makes the intent clear and decouples the act of "getting data" from
 * the act of "forcing a refresh," leading to a more robust and understandable domain layer.
 */
class RefreshMovieListUseCase @Inject constructor(
    private val repository: MovieListRepository,
) {
    /**
     * Triggers a refresh for a specific movie category and language.
     *
     * This operation will delegate to the repository to clear any cached data for the
     * given category, which will in turn cause the PagingSource to invalidate and fetch
     * fresh data from the network on the next collection.
     *
     * @param category The [MovieCategory] to refresh.
     * @param language The language parameter for which to clear the cache.
     */
    suspend operator fun invoke(category: MovieCategory, language: String) {
        repository.refreshCategory(category, language)
    }
}