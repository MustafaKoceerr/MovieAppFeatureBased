package com.mustafakocer.feature_movies.home.data.repository

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

// Dosya: home/data/repository/MovieRepositoryImpl.kt

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val movieApiService: MovieApiService,
    private val homeMovieDao: HomeMovieDao,
    private val languageProvider: LanguageProvider,
) : HomeRepository {

    override fun getMoviesForCategory(category: MovieCategory): Flow<Resource<List<MovieListItem>>> =
        flow {
            // 1. Yüklemenin başladığını bildir.
            emit(Resource.Loading)

            val language = languageProvider.getLanguageParam()
            val categoryKey = category.apiEndpoint

            try {
                // 2. Önbellekteki veriyi al ve hemen yayınla.
                // Bu, kullanıcıya anında bir UI gösterir.
                val cachedMovies = homeMovieDao.getMoviesForCategory(categoryKey, language)
                emit(Resource.Success(cachedMovies.toDomainList()))

                // 3. Ağdan yeni veriyi çek.
                val response = fetchMoviesFromApi(category)

                if (response.isSuccessful && response.body() != null) {
                    val moviesDto = response.body()!!.results ?: emptyList()

                    // 4. Önbelleği yeni veriyle güncelle (atomik işlem).
                    // Önce eski veriyi sil, sonra yenisini ekle.
                    homeMovieDao.deleteMoviesForCategory(categoryKey, language)
                    val movieEntities = moviesDto.toHomeMovieEntityList(categoryKey, language)
                    homeMovieDao.upsertAll(movieEntities)

                    // 5. Güncellenmiş önbelleği Tek Gerçek Kaynağı (Single Source of Truth) olarak tekrar yayınla.
                    val updatedCache = homeMovieDao.getMoviesForCategory(categoryKey, language)
                    emit(Resource.Success(updatedCache.toDomainList()))
                } else {
                    // Ağdan yanıt alınamadı veya yanıt başarısız oldu.
                    // Hata yayınla, ancak UI'da hala eski önbellek verisi gösteriliyor olacak.
                    val exception = ErrorMapper.mapHttpErrorResponseToAppException(response)
                    emit(Resource.Error(exception))
                }
            } catch (e: Exception) {
                // Ağ isteği sırasında bir istisna oluştu (örn. internet yok).
                // Hatayı bizim anladığımız dile çevir ve yayınla.
                val exception = ErrorMapper.mapThrowableToAppException(e)
                emit(Resource.Error(exception))
            }
        }

    /**
     * Kategoriye göre ilgili API endpoint'ini çağıran özel yardımcı fonksiyon.
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
