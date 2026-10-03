package com.mustafakocer.movieappfeaturebasedclean.feature.search

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.api.MovieApiService
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.model.MovieDto
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.model.PaginatedResponseDto
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import com.mustafakocer.movieappfeaturebasedclean.feature.search.data.repository.SearchRepository
import com.mustafakocer.movieappfeaturebasedclean.testutil.MainDispatcherRule
import com.mustafakocer.movieappfeaturebasedclean.testutil.httpError
import com.mustafakocer.movieappfeaturebasedclean.testutil.movieDto
import com.mustafakocer.movieappfeaturebasedclean.testutil.paginatedResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response

class SearchRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val language = MutableStateFlow(LanguagePreference.ENGLISH)
    private var response: Response<PaginatedResponseDto<MovieDto>> =
        Response.success(paginatedResponse(listOf(movieDto(1, "Matrix"), movieDto(2, "Matrix Reloaded"))))
    private val requestedQueries = mutableListOf<Pair<String, Int>>()

    private val api: MovieApiService = Mockito.mock(MovieApiService::class.java) { invocation ->
        if (invocation.method.name == "searchMovies") {
            requestedQueries += invocation.getArgument<String>(0) to invocation.getArgument<Int>(1)
            response
        } else {
            null
        }
    }

    private val languageRepository: LanguageRepository =
        Mockito.mock(LanguageRepository::class.java) { invocation ->
            if (invocation.method.name == "getLanguageFlow") language else null
        }

    private val repository = SearchRepository(api, languageRepository)

    private fun search(query: String): Flow<PagingData<MovieListItem>> =
        repository.searchMovies(MutableStateFlow(query))

    /**
     * `asSnapshot` never finishes for an intentionally empty PagingData, so give it a short real-time
     * budget: null (nothing loaded) is the expected result for queries that must not be searched.
     */
    private suspend fun Flow<PagingData<MovieListItem>>.snapshotOrNull(): List<MovieListItem>? =
        withContext(Dispatchers.Default) { withTimeoutOrNull(500) { asSnapshot() } }

    @Test
    fun `a valid query returns the mapped results`() = runTest {
        val items = search("matrix").asSnapshot()

        assertEquals(listOf(1, 2), items.map { it.id })
        assertEquals(listOf("Matrix", "Matrix Reloaded"), items.map { it.title })
        assertEquals(listOf("2024", "2024"), items.map { it.releaseYear })
    }

    @Test
    fun `the query is trimmed before it is sent to the api`() = runTest {
        search("  matrix  ").asSnapshot()

        assertTrue(requestedQueries.isNotEmpty())
        assertEquals(setOf("matrix"), requestedQueries.map { it.first }.toSet())
        assertEquals(1, requestedQueries.first().second)
    }

    @Test
    fun `a query that is too short is not searched`() = runTest {
        listOf("ab", "   ", "").forEach { query ->
            val items = search(query).snapshotOrNull()

            assertTrue("'$query' must not produce results", items.isNullOrEmpty())
        }
        assertTrue(requestedQueries.isEmpty())
    }

    @Test
    fun `a query that is too long is not searched`() = runTest {
        val items = search("a".repeat(51)).snapshotOrNull()

        assertTrue(items.isNullOrEmpty())
        assertTrue(requestedQueries.isEmpty())
    }

    @Test
    fun `a new result set is emitted when the language changes`() = runTest {
        search("matrix").test {
            awaitItem()

            language.value = LanguagePreference.GERMAN

            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a new result set is emitted when the query changes`() = runTest {
        val query = MutableStateFlow("mat")

        repository.searchMovies(query).test {
            awaitItem()

            query.value = "matrix"

            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `an api failure surfaces as an app exception`() = runTest {
        response = httpError(500)

        val failure = runCatching { search("matrix").asSnapshot() }.exceptionOrNull()

        assertTrue(failure is AppException)
    }
}
