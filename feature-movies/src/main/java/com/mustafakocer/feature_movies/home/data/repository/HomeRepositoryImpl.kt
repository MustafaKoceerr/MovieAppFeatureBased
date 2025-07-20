package com.mustafakocer.feature_movies.home.data.repository

import com.mustafakocer.core_common.util.Resource
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

// Dosya: home/data/repository/MovieRepositoryImpl.kt

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService,
    private val homeMovieDao: HomeMovieDao,
    private val languageProvider: LanguageProvider,
) : HomeRepository {

    private fun getMovies(
        category: MovieCategory,
        apiCall: suspend () -> Response<PaginatedResponseDto<MovieDto>>,
    ): Flow<Resource<List<MovieListItem>>> = flow {

        // 1. Akış başladığında yükleniyor durumunu yayınla
        emit(Resource.Loading)

        val language = languageProvider.getLanguageParam()
        val categoryKey = category.apiEndpoint

        // 2. Cache'deki mevcut veriyi al.
        val cachedMovies = homeMovieDao.getMoviesForCategory(categoryKey, language)

        // 3. Cache'deki veriyi ilk olarak yayınca. UI anında güncellensin.
        emit(Resource.Success(cachedMovies.toDomainList()))

        // 4. Ağdan yeni veriyi çekmeyi dene
        try {
            val response = apiCall()

            if (response.isSuccessful && response.body() != null) {
                val moviesDto = response.body()!!.results ?: emptyList()

                // 5. Veritabanını yeni veriyle güncelle.
                val moviesEntities = moviesDto.toHomeMovieEntityList(categoryKey, language)
                homeMovieDao.deleteMoviesForCategory(categoryKey, language)
                homeMovieDao.upsertAll(moviesEntities)

                // 6. Veritabanını güncelledikten sonra, en güncel veriyi tekrar yayınla.
                // Not: Burada veri tabanına yazdığımız veriyi, elimizde dto'lar olmasına rağmen Single Source of Truth prensibine uymak için veritabanından okuyoruz.
                val updatedCache = homeMovieDao.getMoviesForCategory(categoryKey, language)
                emit(Resource.Success(updatedCache.toDomainList()))
            } else {
                // Ağdan hata geldiyse, bunu bir AppException'a çevirip Error olarak yayınla.
                // Kullanıcı hale cache'deki veriyi görmeye devam eder, ama viewModel hatadan haberdar olur.
                val exception = ErrorMapper.mapHttpErrorResponseToAppException(response)
                emit(Resource.Error(exception))
            }
        } catch (e: Exception) {
            // Ağ isteği tamamen çökerse (örn: internet yok), bunu da Error olarak yayınla.
            val exception = ErrorMapper.mapThrowableToAppException(e)
            emit(Resource.Error(exception))
        }
    }

    override fun getNowPlayingMovies(page: Int): Flow<Resource<List<MovieListItem>>> {
        return getMovies(MovieCategory.NOW_PLAYING) { movieApiService.getNowPlayingMovies(page) }
    }

    override fun getPopularMovies(page: Int): Flow<Resource<List<MovieListItem>>> {
        return getMovies(MovieCategory.POPULAR) { movieApiService.getPopularMovies(page) }
    }

    override fun getTopRatedMovies(page: Int): Flow<Resource<List<MovieListItem>>> {
        return getMovies(MovieCategory.TOP_RATED) { movieApiService.getTopRatedMovies(page) }
    }

    override fun getUpcomingMovies(page: Int): Flow<Resource<List<MovieListItem>>> {
        return getMovies(MovieCategory.UPCOMING) { movieApiService.getUpcomingMovies(page) }
    }
}

/**
 * Bu Yeni Yapı Nasıl Çalışır?
 * ViewModel bu Flow'u dinlemeye başlar.
 * Anında: Resource.Loading gelir. ViewModel, state.isLoading = true yapar.
 * Hemen Ardından: Resource.Success (cache'deki veriyle) gelir. ViewModel, state.isLoading = false ve state.categories = ... yapar. Kullanıcı, internet olmasa bile anında eski veriyi görür.
 * Biraz Sonra (Ağ Başarılıysa): Tekrar Resource.Success (yeni veriyle) gelir. ViewModel, state.categories'i yeni veriyle günceller. UI, taze veriyle kendini yeniler.
 * Biraz Sonra (Ağ Başarısızsa): Resource.Error gelir. ViewModel, state.error = ... yapar. UI, bir Snackbar gösterebilir, ama state.categories'deki eski veri hala durduğu için içerik kaybolmaz.
 */