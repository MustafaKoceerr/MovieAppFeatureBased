package com.mustafakocer.feature_movies.details.data.repository

import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.data.mapper.toDomain
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDetailsRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService, // ← UPDATED: Single service
    private val networkConnectivityMonitor: NetworkConnectivityMonitor, // ✅ ADDED!
) : MovieDetailsRepository {

    /**
     * Get movie details by ID from API.
     *
     * This implementation now has a single responsibility: fetch data from the network,
     * map it to a domain model, or throw an exception if something goes wrong.
     *
     * @param movieId The movie ID
     * @return A COLD Flow that emits the MovieDetails object once.
     */
    override fun getMovieDetails(movieId: Int): Flow<MovieDetails> = flow {
        // `flow { ... }` builder'ı, tek seferlik bir suspend işlemini
        // reaktif bir Flow'a dönüştürmenin en basit yoludur.

        // 1. API'ı çağır.
        val response = movieApiService.getMovieDetails(movieId)

        // 2. Başarı durumunu kontrol et.
        if (response.isSuccessful && response.body() != null) {
            // 3. Başarılıysa, DTO'yu Domain modeline çevir ve FLow'a 'emit' et.
            emit(response.body()!!.toDomain())
        } else {
            // 4. Başarısızsa, HttpException fırlat. Bu hata, ViewModel'deki
            // `executeSafeOnce` tarafından otomatik olarak yakalanacak.
            throw retrofit2.HttpException(response)
        }
    }

}