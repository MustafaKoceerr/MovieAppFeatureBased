package com.mustafakocer.feature_movies.search.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mustafakocer.core_domain.exception.toAppException
import com.mustafakocer.feature_movies.shared.data.api.MovieApiService
import com.mustafakocer.feature_movies.shared.data.mapper.toDomainList
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
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
    private val searchQuery: String,
) : PagingSource<Int, MovieListItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieListItem> {
        return try {
            // Determine which page to load (start from 1)
            val currentPage = params.key ?: 1

            // Make API call
            val response = movieApiService.searchMovies(
                query = searchQuery,
                page = currentPage
            )

            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            val searchResponse = response.body()
                ?: throw Exception("Empty response body")

            // Convert DTOs to domain models

            val movies = searchResponse.results?.map { it.toDomainList() }
// Calculate pagination info
            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (currentPage >= searchResponse.totalPages) null else currentPage + 1

            LoadResult.Page(
                data = movies ?: emptyList(), // Eğer liste cevabı gelmezse emptyList göster.
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

    override fun getRefreshKey(state: PagingState<Int, MovieListItem>): Int? {
        // Return the page that was closest to the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}


