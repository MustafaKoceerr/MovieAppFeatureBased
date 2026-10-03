package com.mustafakocer.movieappfeaturebasedclean.feature.home

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_preferences.provider.LanguageProvider
import com.mustafakocer.movieappfeaturebasedclean.feature.home.data.local.dao.HomeMovieDao
import com.mustafakocer.movieappfeaturebasedclean.feature.home.data.repository.HomeRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.api.MovieApiService
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.local.entity.HomeMovieEntity
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.mapper.toHomeMovieEntity
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.model.MovieDto
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.model.PaginatedResponseDto
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieCategory
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import com.mustafakocer.movieappfeaturebasedclean.testutil.httpError
import com.mustafakocer.movieappfeaturebasedclean.testutil.movieDto
import com.mustafakocer.movieappfeaturebasedclean.testutil.paginatedResponse
import java.io.IOException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response

class HomeRepositoryTest {

    private val language = "en-US"

    // --- fake local cache ---
    private val cache = mutableMapOf<Pair<String, String>, List<HomeMovieEntity>>()
    private val daoCalls = mutableListOf<String>()

    private val dao: HomeMovieDao = Mockito.mock(HomeMovieDao::class.java) { invocation ->
        when (invocation.method.name) {
            "getMoviesForCategory" -> {
                daoCalls += "read"
                cache[invocation.getArgument<String>(0) to invocation.getArgument<String>(1)]
                    ?: emptyList<HomeMovieEntity>()
            }
            "deleteMoviesForCategory" -> {
                daoCalls += "delete"
                cache.remove(invocation.getArgument<String>(0) to invocation.getArgument<String>(1))
                0
            }
            "upsertAll" -> {
                daoCalls += "upsert"
                invocation.getArgument<List<HomeMovieEntity>>(0)
                    .groupBy { it.category to it.language }
                    .forEach { (key, movies) -> cache[key] = (cache[key] ?: emptyList()) + movies }
                Unit
            }
            else -> null
        }
    }

    // --- fake api ---
    private var apiResponse: () -> Response<PaginatedResponseDto<MovieDto>> =
        { Response.success(paginatedResponse(listOf(movieDto(10), movieDto(11)))) }
    private val apiCalls = mutableListOf<String>()

    private val api: MovieApiService = Mockito.mock(MovieApiService::class.java) { invocation ->
        apiCalls += invocation.method.name
        apiResponse()
    }

    private val languageProvider: LanguageProvider =
        Mockito.mock(LanguageProvider::class.java) { invocation ->
            if (invocation.method.name == "getLanguageParam") language else null
        }

    private val repository = HomeRepository(api, dao, languageProvider)

    private fun cached(category: MovieCategory, vararg ids: Int) {
        cache[category.apiEndpoint to language] =
            ids.map { movieDto(it).toHomeMovieEntity(category.apiEndpoint, language) }
    }

    private fun Resource<List<MovieListItem>>.ids(): List<Int> =
        (this as Resource.Success).data.map { it.id }

    @Test
    fun `emits loading then the cache then the fresh network data`() = runTest {
        cached(MovieCategory.POPULAR, 1, 2)

        val emissions = repository.getMoviesForCategory(MovieCategory.POPULAR, isRefresh = false).toList()

        assertEquals(3, emissions.size)
        assertEquals(Resource.Loading, emissions[0])
        assertEquals(listOf(1, 2), emissions[1].ids())
        assertEquals(listOf(10, 11), emissions[2].ids())
    }

    @Test
    fun `the cache is replaced by the fresh data`() = runTest {
        cached(MovieCategory.POPULAR, 1, 2)

        repository.getMoviesForCategory(MovieCategory.POPULAR, isRefresh = false).toList()

        assertEquals(
            listOf(10, 11),
            cache[MovieCategory.POPULAR.apiEndpoint to language]!!.map { it.id }
        )
        assertEquals(listOf("read", "delete", "upsert", "read"), daoCalls)
    }

    @Test
    fun `an empty cache still emits an empty success first`() = runTest {
        val emissions = repository.getMoviesForCategory(MovieCategory.POPULAR, isRefresh = false).toList()

        assertEquals(Resource.Loading, emissions[0])
        assertEquals(emptyList<Int>(), emissions[1].ids())
        assertEquals(listOf(10, 11), emissions[2].ids())
    }

    @Test
    fun `refresh skips the cache and goes straight to the network`() = runTest {
        cached(MovieCategory.POPULAR, 1, 2)

        val emissions = repository.getMoviesForCategory(MovieCategory.POPULAR, isRefresh = true).toList()

        assertEquals(2, emissions.size)
        assertEquals(Resource.Loading, emissions[0])
        assertEquals(listOf(10, 11), emissions[1].ids())
        assertEquals(listOf("delete", "upsert", "read"), daoCalls)
    }

    @Test
    fun `an http error keeps the cache and reports the error`() = runTest {
        cached(MovieCategory.POPULAR, 1, 2)
        apiResponse = { httpError(500) }

        val emissions = repository.getMoviesForCategory(MovieCategory.POPULAR, isRefresh = false).toList()

        assertEquals(listOf(1, 2), emissions[1].ids())
        val error = emissions.last() as Resource.Error
        assertTrue(error.exception is AppException.Api.ServerError)
        assertEquals(listOf(1, 2), cache[MovieCategory.POPULAR.apiEndpoint to language]!!.map { it.id })
    }

    @Test
    fun `a network failure keeps the cache and reports the error`() = runTest {
        cached(MovieCategory.TOP_RATED, 5)
        apiResponse = { throw IOException("offline") }

        val emissions = repository.getMoviesForCategory(MovieCategory.TOP_RATED, isRefresh = false).toList()

        assertEquals(listOf(5), emissions[1].ids())
        val error = emissions.last() as Resource.Error
        assertTrue(error.exception is AppException.Network.NoInternet)
        assertEquals(listOf(5), cache[MovieCategory.TOP_RATED.apiEndpoint to language]!!.map { it.id })
    }

    @Test
    fun `an empty api result clears the cache`() = runTest {
        cached(MovieCategory.POPULAR, 1, 2)
        apiResponse = { Response.success(paginatedResponse<MovieDto>(null)) }

        val emissions = repository.getMoviesForCategory(MovieCategory.POPULAR, isRefresh = false).toList()

        assertEquals(emptyList<Int>(), emissions.last().ids())
        assertTrue(cache[MovieCategory.POPULAR.apiEndpoint to language].isNullOrEmpty())
    }

    @Test
    fun `each category calls its own endpoint`() = runTest {
        MovieCategory.entries.forEach { category ->
            repository.getMoviesForCategory(category, isRefresh = true).toList()
        }

        assertEquals(
            listOf("getNowPlayingMovies", "getPopularMovies", "getTopRatedMovies", "getUpcomingMovies"),
            apiCalls
        )
    }

    @Test
    fun `cache is stored per category and language`() = runTest {
        repository.getMoviesForCategory(MovieCategory.UPCOMING, isRefresh = true).toList()

        assertEquals(setOf(MovieCategory.UPCOMING.apiEndpoint to language), cache.keys)
    }
}
