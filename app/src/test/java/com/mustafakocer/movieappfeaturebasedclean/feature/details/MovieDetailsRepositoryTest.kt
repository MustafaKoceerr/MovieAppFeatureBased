package com.mustafakocer.movieappfeaturebasedclean.feature.details

import app.cash.turbine.test
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.details.data.repository.MovieDetailsRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.api.MovieApiService
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.model.GenreDto
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.model.MovieDetailsDto
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.Genre
import com.mustafakocer.movieappfeaturebasedclean.testutil.MainDispatcherRule
import com.mustafakocer.movieappfeaturebasedclean.testutil.httpError
import com.mustafakocer.movieappfeaturebasedclean.testutil.movieDetailsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response

class MovieDetailsRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val language = MutableStateFlow(LanguagePreference.ENGLISH)
    private var response: Response<MovieDetailsDto> = Response.success(movieDetailsDto(id = 7, title = "Matrix"))
    private val requestedIds = mutableListOf<Int>()

    private val api: MovieApiService = Mockito.mock(MovieApiService::class.java) { invocation ->
        if (invocation.method.name == "getMovieDetails") {
            requestedIds += invocation.getArgument<Int>(0)
            response
        } else {
            null
        }
    }

    private val languageRepository: LanguageRepository =
        Mockito.mock(LanguageRepository::class.java) { invocation ->
            if (invocation.method.name == "getLanguageFlow") language else null
        }

    private val repository = MovieDetailsRepository(api, languageRepository)

    @Test
    fun `emits loading and then the mapped movie details`() = runTest {
        response = Response.success(
            MovieDetailsDto(
                id = 7,
                title = "Matrix",
                overview = "A hacker learns the truth.",
                posterPath = "/poster.jpg",
                releaseDate = "1999-03-31",
                voteAverage = 8.7,
                runtime = 136,
                tagline = "Welcome to the real world",
                genres = listOf(GenreDto(1, "Action"), GenreDto(2, "   "), GenreDto(3, null)),
            )
        )

        repository.getMovieDetails(7).test {
            assertEquals(Resource.Loading, awaitItem())

            val details = (awaitItem() as Resource.Success).data
            assertEquals(7, details.id)
            assertEquals("Matrix", details.title)
            assertEquals("A hacker learns the truth.", details.overview)
            assertEquals("/poster.jpg", details.posterUrl)
            assertEquals("", details.backdropUrl)
            assertEquals("1999-03-31", details.releaseDate)
            assertEquals(8.7, details.voteAverage, 0.0)
            assertEquals(136, details.runtime)
            assertEquals("Welcome to the real world", details.tagline)
            // Genres without a usable name are dropped.
            assertEquals(listOf(Genre(1, "Action")), details.genres)

            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(listOf(7), requestedIds)
    }

    @Test
    fun `missing optional fields are mapped to empty defaults`() = runTest {
        response = Response.success(movieDetailsDto(id = 9, title = null))

        repository.getMovieDetails(9).test {
            awaitItem() // Loading
            val details = (awaitItem() as Resource.Success).data

            assertEquals("", details.title)
            assertEquals("", details.overview)
            assertEquals("", details.posterUrl)
            assertEquals(0, details.runtime)
            assertEquals(0.0, details.voteAverage, 0.0)
            assertEquals(emptyList<Genre>(), details.genres)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `an http error becomes an error resource`() = runTest {
        response = httpError(404)

        repository.getMovieDetails(7).test {
            assertEquals(Resource.Loading, awaitItem())
            val error = awaitItem() as Resource.Error
            assertTrue(error.exception is AppException.Api.NotFound)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `details are fetched again when the app language changes`() = runTest {
        repository.getMovieDetails(7).test {
            assertEquals(Resource.Loading, awaitItem())
            assertTrue(awaitItem() is Resource.Success)
            assertEquals(1, requestedIds.size)

            language.value = LanguagePreference.TURKISH

            assertEquals(Resource.Loading, awaitItem())
            assertTrue(awaitItem() is Resource.Success)
            assertEquals(listOf(7, 7), requestedIds)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `the same language is not fetched twice`() = runTest {
        val emissions = repository.getMovieDetails(7).take(2).toList()

        assertEquals(2, emissions.size)
        assertEquals(1, requestedIds.size)
    }

}
