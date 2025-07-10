package com.mustafakocer.feature_movies.list.data.mediator

import androidx.room.RoomDatabase
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.core_network.connectivity.NetworkConnectivityMonitor
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.list.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Factory for creating MovieListRemoteMediator instances
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Factory Pattern
 * RESPONSIBILITY: Create mediator instances with proper dependencies
 *
 * WHY FACTORY PATTERN:
 * - RemoteMediator needs category parameter at runtime
 * - Hilt can't inject runtime parameters directly
 * - Factory pattern solves this dependency injection issue
 */
@Singleton
class MovieListRemoteMediatorFactory @Inject constructor(
    private val apiService: MovieApiService, // ← UPDATED: Single service
    private val movieListDao: MovieListDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val database: RoomDatabase,
    private val networkConnectivityMonitor: NetworkConnectivityMonitor, // ✅ ADDED!
    @Named("tmdb_api_key") private val apiKey: String,
) {
    /**
     * Create RemoteMediator for specific category
     *
     * @param category Movie category for pagination
     * @return Configured RemoteMediator instance
     */
    fun create(category: MovieCategory): MovieListRemoteMediator {
        return MovieListRemoteMediator(
            apiService = apiService,
            movieListDao = movieListDao,
            remoteKeyDao = remoteKeyDao,
            database = database,
            networkConnectivityMonitor = networkConnectivityMonitor, // ✅ INJECT!
            category = category,
            apiKey = apiKey
        )
    }
}