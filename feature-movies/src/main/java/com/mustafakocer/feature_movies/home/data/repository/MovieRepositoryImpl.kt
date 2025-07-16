package com.mustafakocer.feature_movies.home.data.repository

import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.feature_movies.home.domain.repository.MovieRepository
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.data.mapper.toDomainList
import com.mustafakocer.feature_movies.shared.data.model.MovieDto
import com.mustafakocer.feature_movies.shared.data.model.PaginatedResponseDto
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
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
) : MovieRepository {

    // Dört fonksiyonun da mantığı aynı olduğu için bu özel yardımcı fonksiyonu kullanalım.
    // Bu, kod tekrarını tamamen ortadan kaldırır.
    private fun fetchMoviesFromApi(
        apiCall: suspend () -> Response<PaginatedResponseDto<MovieDto>>,
    ): Flow<List<MovieListItem>> = flow {
        val response = apiCall()
        if (response.isSuccessful && response.body() != null) {
            // Gelen DTO listesini, domain model listesine çevirip emit ediyoruz.
            response.body()!!.results?.let { results ->
                emit(results.map { it.toDomainList() })
            }
        } else {
            // Hata durumunda, ViewModel'in yakalaması için HttpException fırlatıyoruz.
            throw retrofit2.HttpException(response)
        }
    }

    override fun getNowPlayingMovies(page: Int): Flow<List<MovieListItem>> {
        return fetchMoviesFromApi { movieApiService.getNowPlayingMovies(page) }
    }

    override fun getPopularMovies(page: Int): Flow<List<MovieListItem>> {
        return fetchMoviesFromApi { movieApiService.getPopularMovies(page) }
    }

    override fun getTopRatedMovies(page: Int): Flow<List<MovieListItem>> {
        return fetchMoviesFromApi { movieApiService.getTopRatedMovies(page) }
    }

    override fun getUpcomingMovies(page: Int): Flow<List<MovieListItem>> {
        return fetchMoviesFromApi { movieApiService.getUpcomingMovies(page) }
    }
}