package com.mustafakocer.feature_movies.home.data.repository

import com.mustafakocer.core_preferences.provider.LanguageProvider
import com.mustafakocer.feature_movies.home.data.local.HomeMovieDao
import com.mustafakocer.feature_movies.home.domain.repository.MovieRepository
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
// Dosya: home/data/repository/MovieRepositoryImpl.kt

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService,
    private val homeMovieDao: HomeMovieDao,
    private val languageProvider: LanguageProvider,
) : MovieRepository {

    private fun getMoviesForCategory(
        category: MovieCategory,
        fetchFromApi: suspend () -> Response<PaginatedResponseDto<MovieDto>>
    ): Flow<List<MovieListItem>> = flow {
        val language = languageProvider.getLanguageParam()
        val categoryKey = category.apiEndpoint

        // 1. Cache'den ilk veriyi bir kere al ve yayınla.
        val initialCache = homeMovieDao.getMoviesForCategory(categoryKey, language)
        emit(initialCache.toDomainList())

        // 2. Ağdan veriyi çek.
        try {
            val response = fetchFromApi()

            if (response.isSuccessful && response.body() != null) {
                val moviesDto = response.body()!!.results ?: emptyList()

                // 3. Veritabanını güncelle.
                val movieEntities = moviesDto.toHomeMovieEntityList(categoryKey, language)
                homeMovieDao.deleteMoviesForCategory(categoryKey, language)
                homeMovieDao.upsertAll(movieEntities)

                // 4. Veritabanı güncellendikten sonra, en güncel veriyi tekrar yayınla.
                val updatedCache = homeMovieDao.getMoviesForCategory(categoryKey, language)
                emit(updatedCache.toDomainList())

            }
        } catch (e: Exception) {
        }
    }

    override fun getNowPlayingMovies(page: Int): Flow<List<MovieListItem>> {
        return getMoviesForCategory(MovieCategory.NOW_PLAYING) { movieApiService.getNowPlayingMovies(page) }
    }

    override fun getPopularMovies(page: Int): Flow<List<MovieListItem>> {
        return getMoviesForCategory(MovieCategory.POPULAR) { movieApiService.getPopularMovies(page) }
    }

    override fun getTopRatedMovies(page: Int): Flow<List<MovieListItem>> {
        return getMoviesForCategory(MovieCategory.TOP_RATED) { movieApiService.getTopRatedMovies(page) }
    }

    override fun getUpcomingMovies(page: Int): Flow<List<MovieListItem>> {
        return getMoviesForCategory(MovieCategory.UPCOMING) { movieApiService.getUpcomingMovies(page) }
    }
}