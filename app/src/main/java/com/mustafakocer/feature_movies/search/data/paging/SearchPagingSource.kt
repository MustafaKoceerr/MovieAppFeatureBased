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
 * A [PagingSource] that fetches paginated search results directly from the network API.
 *
 * Architectural Decision: This PagingSource implements a network-only pagination strategy.
 * It does not interact with a local database, which is a suitable approach for volatile data
 * like search results where caching is not required and real-time data is preferred.
 *
 * Its responsibilities include:
 * - Fetching a specific page of search results from the [MovieApiService].
 * - Managing the pagination logic by calculating the next and previous page keys.
 * - Converting the network Data Transfer Objects (DTOs) into domain models.
 * - Handling loading states and errors, wrapping them in a [LoadResult].
 */
class SearchPagingSource(
    private val movieApiService: MovieApiService,
    private val searchQuery: String,
) : PagingSource<Int, MovieListItem>() {

    /**
     * The core function of the PagingSource, responsible for fetching a single page of data.
     *
     * This function is called by the Paging library automatically when the UI needs to display
     * more items.
     *
     * @param params Contains information about the page to be loaded, including the key (page number)
     *               and the requested load size.
     * @return A [LoadResult] object, which can be a [LoadResult.Page] on success or a
     *         [LoadResult.Error] on failure.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieListItem> {
        return try {
            // Determine the page number to fetch. `params.key` is null for the initial load.
            val currentPage = params.key ?: 1

            // Execute the network API call to search for movies.
            val response = movieApiService.searchMovies(
                query = searchQuery,
                page = currentPage
            )

            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            // If the response body is null, it's treated as an unrecoverable error.
            val searchResponse = response.body()
                ?: return LoadResult.Error(Exception("Empty response body"))

            // The list of DTOs from the network is mapped to a list of domain models.
            // This ensures the rest of the app is decoupled from the specific data layer models.
            val movies = searchResponse.results?.toDomainList() ?: emptyList()

            // Calculate the keys for the previous and next pages.
            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (currentPage >= searchResponse.totalPages || movies.isEmpty()) null else currentPage + 1

            LoadResult.Page(
                data = movies,
                prevKey = prevPage,
                nextKey = nextPage
            )

        } catch (e: IOException) {
            // IOException typically indicates a network failure (e.g., no internet connection).
            LoadResult.Error(e.toAppException())
        } catch (e: HttpException) {
            // HttpException indicates a non-2xx response from the server (e.g., 404 Not Found, 500 Server Error).
            LoadResult.Error(e.toAppException())
        } catch (e: Exception) {
            // Catch any other unexpected exceptions to prevent crashes.
            LoadResult.Error(e.toAppException())
        }
    }

    /**
     * Provides a key for the Paging library to use when the data is invalidated and needs to be
     * reloaded.
     *
     * The goal is to load data around the user's last scroll position to provide a seamless
     * experience after a refresh.
     *
     * @param state The current [PagingState], which contains information about the pages that have
     *              been loaded so far.
     * @return The key (page number) to be passed to the `load` function, or null if a key cannot
     *         be determined.
     */
    override fun getRefreshKey(state: PagingState<Int, MovieListItem>): Int? {
        // We try to find the page key that was closest to the user's last viewing position.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            // If we have an anchor page, we can determine the refresh key from its prev/next keys.
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}