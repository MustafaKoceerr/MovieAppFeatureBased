package com.mustafakocer.feature_movies.home.data.repository

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.core_common.result.map
import com.mustafakocer.core_network.api.safeApiCall
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.core_network.error.applyRetryStrategy
import com.mustafakocer.core_network.utils.ensureConnected
import com.mustafakocer.feature_movies.home.data.mapper.toDomainModels
import com.mustafakocer.feature_movies.home.domain.model.Movie
import com.mustafakocer.feature_movies.home.domain.repository.MovieRepository
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * TEACHING MOMENT: Clean Repository Implementation
 *
 * ✅ BENEFITS:
 * - Each method handles specific endpoint
 * - Consistent error handling with safeApiCall
 * - Clean data transformation with mappers
 * - Easy to test individual methods
 * - API key injection for security
 */

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService, // ← UPDATED: Single service
    private val networkConnectivityMonitor: NetworkConnectivityMonitor, // ✅ ADDED!
    @Named("tmdb_api_key") private val apiKey: String,
) : MovieRepository {

    override fun getNowPlayingMovies(page: Int): Flow<UiState<List<Movie>>> = flow {
        if (!checkConnectivity()) return@flow // ✅ CONNECTIVITY CHECK!

        safeApiCall(
            apiCall = { movieApiService.getNowPlayingMovies(apiKey, page) },
            loadingMessage = "Loading now playing movies..."
        ).map { uiState ->
            uiState.map { response ->
                response.results.toDomainModels()
            }
        }.applyRetryStrategy() // ✅ RETRY STRATEGY ADDED!
            .collect { emit(it) }
    }

    override fun getPopularMovies(page: Int): Flow<UiState<List<Movie>>> = flow {
        if (!ensureConnected(networkConnectivityMonitor)) return@flow // ✅ CLEAN UTILITY!

        safeApiCall(
            apiCall = { movieApiService.getPopularMovies(apiKey, page) },
            loadingMessage = "Loading popular movies..."
        ).map { uiState ->
            uiState.map { response ->
                response.results.toDomainModels()
            }
        }.applyRetryStrategy() // ✅ RETRY STRATEGY ADDED!
            .collect { emit(it) }
    }

    override fun getTopRatedMovies(page: Int): Flow<UiState<List<Movie>>> = flow {
        if (!ensureConnected(networkConnectivityMonitor)) return@flow // ✅ CLEAN UTILITY!

        safeApiCall(
            apiCall = { movieApiService.getTopRatedMovies(apiKey, page) },
            loadingMessage = "Loading top rated movies..."
        ).map { uiState ->
            uiState.map { response ->
                response.results.toDomainModels()
            }
        }.applyRetryStrategy() // ✅ RETRY STRATEGY ADDED!
            .collect { emit(it) }
    }

    override fun getUpcomingMovies(page: Int): Flow<UiState<List<Movie>>> = flow {
        if (!ensureConnected(networkConnectivityMonitor)) return@flow // ✅ CLEAN UTILITY!

        safeApiCall(
            apiCall = { movieApiService.getUpcomingMovies(apiKey, page) },
            loadingMessage = "Loading upcoming movies..."
        ).map { uiState ->
            uiState.map { response ->
                response.results.toDomainModels()
            }
        }.applyRetryStrategy() // ✅ RETRY STRATEGY ADDED!
            .collect { emit(it) }
    }

    // ==================== PRIVATE HELPER ====================

    /**
     * Check network connectivity before making API call
     * Returns true if connected, emits error and returns false if not
     */
    private suspend fun FlowCollector<UiState<List<Movie>>>.checkConnectivity(): Boolean {
        val connectionState = networkConnectivityMonitor.getCurrentConnectionState()
        if (!connectionState.isConnected) {
            emit(UiState.Error(AppException.NetworkException.NoInternetConnection()))
            return false
        }
        return true
    }
}