package com.mustafakocer.feature_movies.details.domain.usecase

import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieDetailsRepository,
) {
    operator fun invoke(movieId: Int): Flow<Resource<MovieDetails>> {
        // BUSINESS RULE: Movie ID must be positive
        require(movieId > 0) { "Movie ID must be positive, but was: $movieId" }

        // DELEGATION: Repository handles data concerns
        return repository.getMovieDetails(movieId)
    }
}
