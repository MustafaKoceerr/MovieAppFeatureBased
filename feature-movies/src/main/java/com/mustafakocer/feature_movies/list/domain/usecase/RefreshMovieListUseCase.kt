package com.mustafakocer.feature_movies.list.domain.usecase

import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.list.domain.repository.MovieListRepository
import javax.inject.Inject

/**
 * Refresh movie list use case
 *
 * CLEAN ARCHITECTURE: Domain Layer - Business Logic
 * RESPONSIBILITY: Handle movie list refresh operations
 */
class RefreshMovieListUseCase @Inject constructor(
    private val repository: MovieListRepository,
) {
    /**
     * Refresh movie list for specific category
     * Clear cache and triggers fresh data fetch
     *
     * @param category Movie category to refresh
     */
    suspend operator fun invoke(category: MovieCategory) {
        repository.refreshCategory(category)
    }
}