package com.mustafakocer.feature_movies.list.data.mediator

import androidx.room.RoomDatabase
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A factory for creating [MovieListRemoteMediator] instances.
 *
 * Architectural Decision: The Factory Pattern is employed here to solve a common dependency
 * injection challenge with Jetpack Paging's `RemoteMediator`. A `RemoteMediator` often needs
 * parameters that are only known at runtime (in this case, the `category` and `language` for the
 * movie list).
 *
 * Since Hilt can only inject compile-time dependencies, it cannot directly create a
 * `RemoteMediator` that requires runtime data. This factory acts as a bridge:
 * 1. Hilt injects the compile-time dependencies (`apiService`, DAOs, `database`) into this factory.
 * 2. The `MovieListRepositoryImpl` then calls the `create` method on this factory, passing the
 *    necessary runtime parameters.
 * This approach ensures proper dependency injection while allowing for dynamic `RemoteMediator`
 * instantiation.
 */
@Singleton
class MovieListRemoteMediatorFactory @Inject constructor(
    private val apiService: MovieApiService,
    private val movieListDao: MovieListDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val database: RoomDatabase,
) {
    /**
     * Creates a fully configured [MovieListRemoteMediator] instance for a specific movie category
     * and language.
     *
     * @param category The movie category for which the mediator will fetch paginated data.
     * @param language The language parameter for the API requests.
     * @return A new, configured instance of [MovieListRemoteMediator].
     */
    fun create(
        category: MovieCategory,
        language: String,
    ): MovieListRemoteMediator {
        return MovieListRemoteMediator(
            apiService = apiService,
            movieListDao = movieListDao,
            remoteKeyDao = remoteKeyDao,
            database = database,
            category = category,
            language = language
        )
    }
}