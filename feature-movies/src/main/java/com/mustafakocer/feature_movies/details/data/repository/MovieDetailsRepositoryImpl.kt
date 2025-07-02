package com.mustafakocer.feature_movies.details.data.repository

import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.core_common.result.map
import com.mustafakocer.core_network.api.safeApiCall
import com.mustafakocer.feature_movies.details.data.mapper.toDomain
import com.mustafakocer.feature_movies.details.data.remote.MovieDetailsApiService
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Movie details repository implementation
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer
 * RESPONSIBILITY: Implement movie details data operations
 */
@Singleton
class MovieDetailsRepositoryImpl @Inject constructor(
    private val apiService: MovieDetailsApiService,
    @Named("tmdb_api_key") private val apiKey: String
) : MovieDetailsRepository {

    override fun getMovieDetails(movieId: Int): Flow<UiState<MovieDetails>> = flow {
        safeApiCall(
            apiCall = { apiService.getMovieDetails(movieId, apiKey) },
            loadingMessage = "Loading movie details..."
        ).map { uiState ->
            uiState.map { dto ->
                dto.toDomain()
            }
        }.collect { emit(it) }
    }
}
