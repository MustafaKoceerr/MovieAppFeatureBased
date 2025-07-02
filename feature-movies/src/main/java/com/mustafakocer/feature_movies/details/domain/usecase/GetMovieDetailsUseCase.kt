package com.mustafakocer.feature_movies.details.domain.usecase

import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting movie details
 *
 * CLEAN ARCHITECTURE: Domain Layer
 * RESPONSIBILITY: Coordinate movie details retrieval business logic
 */
class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieDetailsRepository,
) {
    /**
     * Get movie details by ID
     *
     * @param movieId The movie ID
     * @return Flow of UiState with movie details
     */
    operator fun invoke(movieId: Int): Flow<UiState<MovieDetails>> {
        return repository.getMovieDetails(movieId)
    }
}