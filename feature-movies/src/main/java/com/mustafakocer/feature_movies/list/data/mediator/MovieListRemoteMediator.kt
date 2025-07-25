package com.mustafakocer.feature_movies.list.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.core_database.pagination.RemoteKey
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity
import com.mustafakocer.feature_movies.shared.data.mapper.toEntityList
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MovieListRemoteMediator(
    private val apiService: MovieApiService,
    private val movieListDao: MovieListDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val database: RoomDatabase,
    private val category: MovieCategory,
    private val language: String,
) : RemoteMediator<Int, MovieListEntity>() {

    companion object {
        private const val STARTING_PAGE = 1
    }

    override suspend fun initialize(): InitializeAction {
        // Bu mantık doğru ve kalabilir.
        val hasCachedData = movieListDao.hasCachedDataForCategory(category.apiEndpoint, language)
        return if (hasCachedData) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieListEntity>,
    ): MediatorResult {
        return try {
            // Sayfa numarasını belirlemek için daha basit ve daha sağlam bir mantık.
            val page = when (loadType) {
                // REFRESH her zaman ilk sayfadan başlar.
                LoadType.REFRESH -> STARTING_PAGE

                // PREPEND (yukarı kaydırma) bu projede desteklenmiyor.
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

                // APPEND (aşağı kaydırma) için, bir sonraki sayfayı doğrudan DAO'dan al.
                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.getRemoteKey(category.cacheKey, language)
                    val nextPage = remoteKey?.nextKey?.toIntOrNull()
                    if (nextPage == null) {
                        // Eğer bir sonraki sayfa anahtarı yoksa, listenin sonuna gelinmiştir.
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    nextPage
                }
            }

            val apiResponse = apiService.getMoviesByCategory(
                category = category.apiEndpoint,
                page = page,
            )

            if (!apiResponse.isSuccessful) throw HttpException(apiResponse)
            val body = apiResponse.body() ?: return MediatorResult.Success(endOfPaginationReached = true)

            val movies = body.results ?: emptyList()
            val endOfPaginationReached = movies.isEmpty() || (page >= body.totalPages)

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieListDao.deleteMoviesForCategory(category.apiEndpoint, language)
                    remoteKeyDao.deleteRemoteKey(category.cacheKey, language)
                }

                val remoteKey = RemoteKey.create(
                    query = category.cacheKey,
                    language = language,
                    currentPage = page,
                    totalPages = body.totalPages,
                    totalItems = body.totalResults
                )
                remoteKeyDao.upsert(remoteKey)

                val movieEntities = movies.toEntityList(
                    category = category.apiEndpoint,
                    page = page,
                    pageSize = state.config.pageSize,
                    language = language
                )
                movieListDao.upsertAll(movieEntities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    // Karmaşık ve hataya açık helper metotları TAMAMEN KALDIRILDI.
}