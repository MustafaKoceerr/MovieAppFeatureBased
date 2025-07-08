package com.mustafakocer.feature_movies.details.domain.usecase

import com.mustafakocer.core_common.result.NetworkAwareUiState
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
    operator fun invoke(movieId: Int): Flow<NetworkAwareUiState<MovieDetails>> {
        // BUSINESS RULE: Movie ID must be positive
        require(movieId > 0) { "Movie ID must be positive, but was: $movieId" }

        // DELEGATION: Repository handles data concerns
        return repository.getMovieDetails(movieId)
    }
}
