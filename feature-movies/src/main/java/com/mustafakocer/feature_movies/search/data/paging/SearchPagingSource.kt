package com.mustafakocer.feature_movies.search.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.util.query
import com.mustafakocer.core_common.exception.toAppException
import com.mustafakocer.feature_movies.search.data.mapper.toDomain
import com.mustafakocer.feature_movies.search.domain.model.SearchQuery
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.domain.model.Movie
import retrofit2.HttpException
import java.io.IOException

/**
 * PagingSource for search results
 *
 * Paging 3 architecture: Direct API pagination without database
 * Responsibility:
 * - Fetch search results from API
 * - Handle pagination logic
 * - Convert DTOs to domain models
 * - Manage loading states
 */

class SearchPagingSource(
    private val movieApiService: MovieApiService,
    private val apiKey: String,
    private val searchQuery: String,
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            // Determine which page to load (start from 1)
            val currentPage = params.key ?: 1

            // Make API call
            val response = movieApiService.searchMovies(
                apiKey = apiKey,
                query = searchQuery,
                page = currentPage
            )

            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            val searchResponse = response.body()
                ?: throw Exception("Empty response body")

            // Convert DTOs to domain models
            val movies = searchResponse.results.map { it.toDomain() }
// Calculate pagination info
            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (currentPage >= searchResponse.total_pages) null else currentPage + 1

            LoadResult.Page(
                data = movies,
                prevKey = prevPage,
                nextKey = nextPage
            )

        } catch (e: IOException) {
            LoadResult.Error(e.toAppException())
        } catch (e: HttpException) {
            LoadResult.Error(e.toAppException())
        } catch (e: Exception) {
            LoadResult.Error(e.toAppException())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        // Return the page that was closest to the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}


