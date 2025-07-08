package com.mustafakocer.feature_movies.details.data.repository

import com.mustafakocer.core_common.result.NetworkAwareUiState
import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.core_network.extensions.createInitializeOnceNetworkAwareFlow
import com.mustafakocer.core_network.extensions.createNetworkFlow
import com.mustafakocer.feature_movies.details.data.mapper.toDomain
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Repository pattern - Abstract data source complexity
 * Dependency ınversion- depends on abstractions (interfaces)
 * Single responsibility - Only handles movie details data operations
 * Open/Closed - Open for extension (new data sources), closed for modification
 *
 * Network aware pattern:
 * Uses initialize-once strategy for detail screens
 * Preserves data when connectivity is lost
 * Automatically refreshes when connectivity is restored
 * Provides different UX based on data availability
 *
 * Anti patterns avoided
 * No god objects - focused responsibility
 * No direct UI coupling - returns domain models
 * No business logic - pure data operations
 * No framework coupling - framework agnostic
 */

@Singleton
class MovieDetailsRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService, // ← UPDATED: Single service
    private val networkConnectivityMonitor: NetworkConnectivityMonitor, // ✅ ADDED!
    @Named("tmdb_api_key") private val apiKey: String,
) : MovieDetailsRepository {
    /**
     * Get movie details with network-aware data preservation
     *
     * STRATEGY: Initialize-Once Pattern
     * - Perfect for detail screens that don't need constant refreshing
     * - Loads data once, preserves it during connectivity issues
     * - Only refreshes when connectivity is restored after being lost
     *
     * BEHAVIOR SCENARIOS:
     * 1. First load with internet → Normal loading → Success
     * 2. First load without internet → Error screen
     * 3. Has data + internet lost → Preserve data + show snackbar
     * 4. Has data + internet restored → Refresh data automatically
     * 5. Refresh failed with existing data → Preserve data + show error snackbar
     */
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

    /**
     * Alternative method for manual refresh scenarios
     *
     * Can be used if you want to provide a "refresh" button or
     * pull-to-refresh functionality in the details screen
     */

    fun refreshMovieDetails(movieId: Int): Flow<NetworkAwareUiState<MovieDetails>> {
        return createInitializeOnceNetworkAwareFlow(
            networkMonitor = networkConnectivityMonitor
        ) {
            val response = movieApiService.getMovieDetails(movieId, apiKey)

            if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                throw retrofit2.HttpException(response)
            }
        }
    }
}
/**
 * TEACHING MOMENT: Why This Repository Design is Clean
 *
 * 1. SINGLE RESPONSIBILITY:
 *    - Only handles movie details data operations
 *    - Doesn't know about UI, navigation, or business rules
 *    - Focuses purely on data fetching and transformation
 *
 * 2. DEPENDENCY INVERSION:
 *    - Depends on abstractions (MovieApiService interface, NetworkConnectivityMonitor)
 *    - Can be easily tested with mocks
 *    - Can swap API implementations without changing this code
 *
 * 3. SEPARATION OF CONCERNS:
 *    - Network logic → Handled by NetworkConnectivityMonitor
 *    - HTTP logic → Handled by MovieApiService
 *    - Data transformation → Handled by mapper functions
 *    - Error mapping → Handled by network-aware flow extensions
 *    - UI state → Handled by NetworkAwareUiState
 *
 * 4. TESTABILITY:
 *    - Can test with fake NetworkConnectivityMonitor
 *    - Can test with fake MovieApiService
 *    - Easy to verify behavior in different connectivity scenarios
 *    - No UI dependencies to mock
 *
 * 5. EXTENSIBILITY:
 *    - Easy to add caching layer (Room database)
 *    - Easy to add different refresh strategies
 *    - Easy to add new API endpoints
 *    - Network-aware pattern can be reused for other repositories
 */