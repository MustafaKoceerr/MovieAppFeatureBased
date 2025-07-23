package com.mustafakocer.feature_movies.home.data.repository

import com.mustafakocer.core_domain.exception.toAppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_network.error.ErrorMapper
import com.mustafakocer.core_preferences.provider.LanguageProvider
import com.mustafakocer.feature_movies.home.data.local.dao.HomeMovieDao
import com.mustafakocer.feature_movies.home.domain.repository.HomeRepository
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.data.mapper.toDomainList
import com.mustafakocer.feature_movies.shared.data.mapper.toHomeMovieEntityList
import com.mustafakocer.feature_movies.shared.data.model.MovieDto
import com.mustafakocer.feature_movies.shared.data.model.PaginatedResponseDto
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implements the [HomeRepository] interface, providing a concrete data-handling strategy.
 *
 * Architectural Decision: This repository follows a "Single Source of Truth" (SSOT) pattern with a
 * cache-first strategy. The local database (via `HomeMovieDao`) is the SSOT for the UI. The repository
 * first emits cached data for immediate display, then fetches fresh data from the network, updates
 * the cache, and finally emits the updated data from the cache again. This provides a responsive
 * user experience and offline support.
 */
@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService,
    private val homeMovieDao: HomeMovieDao,
    private val languageProvider: LanguageProvider,
) : HomeRepository {

    /**
     * Fetches movies for a given category, implementing the cache-first strategy.
     *
     * @param category The category of movies to fetch.
     * @param isRefresh If true, the initial cache emission is skipped, and data is fetched directly
     *                  from the network.
     * @return A Flow emitting resource states for the movie list.
     */
    override fun getMoviesForCategory(
        category: MovieCategory,
        isRefresh: Boolean,
    ): Flow<Resource<List<MovieListItem>>> = flow {
        // 1. Emit Loading state to inform the UI that a data operation has started.
        emit(Resource.Loading)

        val language = languageProvider.getLanguageParam()
        val categoryKey = category.apiEndpoint

        try {
            // 2. Emit cached data first (if not a forced refresh).
            // This provides an instant UI update with potentially stale data, improving perceived performance.
            if (!isRefresh) {
                val cachedMovies = homeMovieDao.getMoviesForCategory(categoryKey, language)
                emit(Resource.Success(cachedMovies.toDomainList()))
            }

            // 3. Fetch fresh data from the network.
            val response = fetchMoviesFromApi(category)

            if (response.isSuccessful && response.body() != null) {
                val moviesDto = response.body()!!.results ?: emptyList()

                // 4. Update the cache with the new network data in an atomic operation.
                // Deleting old data before inserting new data ensures the cache is always consistent.
                homeMovieDao.deleteMoviesForCategory(categoryKey, language)
                val movieEntities = moviesDto.toHomeMovieEntityList(categoryKey, language)
                homeMovieDao.upsertAll(movieEntities)

                // 5. Emit the updated cache as the final, authoritative data.
                // This reinforces the database as the Single Source of Truth.
                val updatedCache = homeMovieDao.getMoviesForCategory(categoryKey, language)
                emit(Resource.Success(updatedCache.toDomainList()))
            } else {
                // If the network call fails, emit an error.
                // The UI can gracefully handle this, for instance, by continuing to show the
                // previously emitted (stale) cached data along with an error message.
                val exception = ErrorMapper.mapHttpErrorResponseToAppException(response)
                emit(Resource.Error(exception))
            }
        } catch (e: Exception) {
            // Catch any other exceptions (e.g., network connectivity issues) and wrap them.
            emit(Resource.Error(e.toAppException()))
        }
    }

    /**
     * A private helper function to encapsulate the logic of calling the correct API endpoint.
     * This keeps the main repository method cleaner and more focused on the caching strategy.
     *
     * @param category The movie category to determine which API endpoint to call.
     * @return The Retrofit [Response] object from the API call.
     */
    private suspend fun fetchMoviesFromApi(category: MovieCategory): Response<PaginatedResponseDto<MovieDto>> {
        return when (category) {
            MovieCategory.NOW_PLAYING -> movieApiService.getNowPlayingMovies()
            MovieCategory.POPULAR -> movieApiService.getPopularMovies()
            MovieCategory.TOP_RATED -> movieApiService.getTopRatedMovies()
            MovieCategory.UPCOMING -> movieApiService.getUpcomingMovies()
        }
    }
}