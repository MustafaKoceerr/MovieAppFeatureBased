package com.mustafakocer.feature_movies.home.data.repository

import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.core_common.result.map
import com.mustafakocer.core_network.api.safeApiCall
import com.mustafakocer.feature_movies.home.data.api.MovieApiService
import com.mustafakocer.feature_movies.home.data.mapper.toDomainModels
import com.mustafakocer.feature_movies.home.domain.model.Movie
import com.mustafakocer.feature_movies.home.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
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
    private val movieApiService: MovieApiService,
    @Named("tmdb_api_key") private val apiKey: String,
) : MovieRepository {
    override fun getNowPlayingMovies(page: Int): Flow<UiState<List<Movie>>> = flow {
        safeApiCall(
            apiCall = { movieApiService.getNowPlayingMovies(apiKey, page) },
            loadingMessage = "Loading popular movies..."
        ).map { uiState ->
            uiState.map { response ->
                response.results.toDomainModels()
            }
        }.collect { emit(it) }
    }

    override fun getPopularMovies(page: Int): Flow<UiState<List<Movie>>> = flow {
        safeApiCall(
            apiCall = { movieApiService.getPopularMovies(apiKey, page) },
            loadingMessage = "Loading popular movies..."
        ).map { uiState ->
            uiState.map { response ->
                response.results.toDomainModels()
            }
        }.collect { emit(it) }
    }

    override fun getTopRatedMovies(page: Int): Flow<UiState<List<Movie>>> = flow {
        safeApiCall(
            apiCall = { movieApiService.getTopRatedMovies(apiKey, page) },
            loadingMessage = "Loading top rated movies..."
        ).map { uiState ->
            uiState.map { response ->
                response.results.toDomainModels()
            }
        }.collect { emit(it) }
    }

    override fun getUpcomingMovies(page: Int): Flow<UiState<List<Movie>>> = flow {
        safeApiCall(
            apiCall = { movieApiService.getUpcomingMovies(apiKey, page) },
            loadingMessage = "Loading upcoming movies..."
        ).map { uiState ->
            uiState.map { response ->
                response.results.toDomainModels()
            }
        }.collect { emit(it) }
    }

}