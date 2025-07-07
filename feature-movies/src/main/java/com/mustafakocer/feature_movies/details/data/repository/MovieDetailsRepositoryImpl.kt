package com.mustafakocer.feature_movies.details.data.repository

import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.core_common.result.map
import com.mustafakocer.core_network.api.safeApiCall
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.core_network.error.applyRetryStrategy
import com.mustafakocer.core_network.utils.ensureConnected
import com.mustafakocer.feature_movies.details.data.mapper.toDomain
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
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
    private val movieApiService: MovieApiService, // ← UPDATED: Single service
    private val networkConnectivityMonitor: NetworkConnectivityMonitor, // ✅ ADDED!
    @Named("tmdb_api_key") private val apiKey: String,
) : MovieDetailsRepository {

    override fun getMovieDetails(movieId: Int): Flow<UiState<MovieDetails>> = flow {
        if (!ensureConnected(networkConnectivityMonitor)) return@flow // ✅ CLEAN UTILITY!

        safeApiCall(
            apiCall = { movieApiService.getMovieDetails(movieId, apiKey) },
            loadingMessage = "Loading movie details..."
        ).map { uiState ->
            uiState.map { dto ->
                dto.toDomain()
            }
        }.applyRetryStrategy() // ✅ RETRY STRATEGY ADDED!
            .collect { emit(it) }
    }
}
