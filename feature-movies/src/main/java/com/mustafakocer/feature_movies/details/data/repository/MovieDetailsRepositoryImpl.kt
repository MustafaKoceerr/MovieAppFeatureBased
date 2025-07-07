package com.mustafakocer.feature_movies.details.data.repository

import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.core_common.result.map
import com.mustafakocer.core_network.api.safeApiCall
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.core_network.error.applyRetryStrategy
import com.mustafakocer.core_network.extensions.createNetworkFlow
import com.mustafakocer.feature_movies.details.data.mapper.toDomain
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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
    // 1. İnternet bağlantısını sürekli dinleyen bir tetikleyici oluştur.
    //    .distinctUntilChanged() sadece durum değiştiğinde (örn: kapalı -> açık) akışı tetikler.
    //    .filter { it.isConnected } sadece "internet geldi" anlarını yakalar.

    override fun getMovieDetails(movieId: Int): Flow<UiState<MovieDetails>> {
        // ✅ CLEAN: Framework-agnostic fonksiyonu çağırıyoruz
        return createNetworkFlow(
            networkMonitor = networkConnectivityMonitor
        ) {
            // ✅ REPOSITORY RESPONSIBILITY: Retrofit detayları burada!
            val response = movieApiService.getMovieDetails(movieId, apiKey)

            // Başarı ve hata kontrolü Repository'nin sorumluluğu
            if (response.isSuccessful && response.body() != null) {
                // DTO'yu domain'e çevir ve döndür
                response.body()!!.toDomain()
            } else {
                // HTTP hatası fırlat, createNetworkFlow yakalayacak
                throw retrofit2.HttpException(response)
            }
        }
    }
}
