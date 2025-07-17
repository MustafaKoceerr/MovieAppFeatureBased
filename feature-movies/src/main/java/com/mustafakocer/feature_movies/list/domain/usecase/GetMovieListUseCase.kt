package com.mustafakocer.feature_movies.list.domain.usecase

import android.util.Log
import androidx.paging.PagingData
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.list.domain.repository.MovieListRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Get paginated movie list use case
 *
 * CLEAN ARCHITECTURE: Domain Layer - Business Logic
 * RESPONSIBILITY: Coordinate movie list retrieval business rules
 *
 *  * DESIGN PATTERN: Use Case Pattern
 *  * - Single responsibility: Get movies for category
 *  * - Business logic encapsulation
 *  * - Testable business operations
 * */
class GetMovieListUseCase @Inject constructor(
    private val repository: MovieListRepository,
) {
    /**
     * Get paginated movies for specific category
     *
     * @param category Movie category to fetch
     * @return Flow of PagingData for reactive pagination
     */
    operator fun invoke(category: MovieCategory, language: String): Flow<PagingData<MovieListItem>> {
        return repository.getMoviesByCategory(category, language)
    }
}