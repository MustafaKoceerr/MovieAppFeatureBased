package com.mustafakocer.feature_movies.details.data.repository

import com.mustafakocer.core_common.result.NetworkAwareUiState
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.core_network.util.createInitializeOnceNetworkAwareFlow
import com.mustafakocer.feature_movies.details.data.mapper.toDomain
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MovieDetailsRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService, // ← UPDATED: Single service
    private val networkConnectivityMonitor: NetworkConnectivityMonitor, // ✅ ADDED!
    @Named("tmdb_api_key") private val apiKey: String,
) : MovieDetailsRepository {

    override fun getMovieDetails(movieId: Int): Flow<NetworkAwareUiState<MovieDetails>> {
        // ✅ CLEAN: Framework-agnostic fonksiyonu çağırıyoruz
        return createInitializeOnceNetworkAwareFlow(
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