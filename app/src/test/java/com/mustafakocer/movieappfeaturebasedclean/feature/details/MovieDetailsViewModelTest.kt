package com.mustafakocer.movieappfeaturebasedclean.feature.details

import androidx.lifecycle.SavedStateHandle
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.movieappfeaturebasedclean.feature.details.data.repository.MovieDetailsRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.viewmodel.MovieDetailsViewModel
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieDetails
import com.mustafakocer.movieappfeaturebasedclean.navigation.MovieDetailsScreen
import com.mustafakocer.movieappfeaturebasedclean.testutil.MainDispatcherRule
import com.mustafakocer.movieappfeaturebasedclean.testutil.movieDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class MovieDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val details = MutableSharedFlow<Resource<MovieDetails>>(extraBufferCapacity = 8)
    private val requestedIds = mutableListOf<Int>()

    private val repository: MovieDetailsRepository =
        Mockito.mock(MovieDetailsRepository::class.java) { invocation ->
            requestedIds += invocation.getArgument<Int>(0)
            details as Flow<*>
        }

    private fun createViewModel(movieId: Int = 42) = MovieDetailsViewModel(
        movieDetailsRepository = repository,
        savedStateHandle = SavedStateHandle(mapOf(MovieDetailsScreen::movieId.name to movieId)),
    )

    @Test
    fun `loads the movie id taken from the route arguments`() = runTest {
        createViewModel(movieId = 42)

        assertEquals(listOf(42), requestedIds)
    }

    @Test
    fun `loading sets isLoading and success sets the movie`() = runTest {
        val viewModel = createViewModel()

        details.emit(Resource.Loading)
        assertTrue(viewModel.uiState.value.isLoading)

        details.emit(Resource.Success(movieDetails(42)))
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(movieDetails(42), state.movie)
        assertNull(state.error)
    }

    @Test
    fun `error is exposed and stops loading`() = runTest {
        val viewModel = createViewModel()
        details.emit(Resource.Loading)

        details.emit(Resource.Error(AppException.Api.NotFound()))

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertNull(state.movie)
    }

    @Test
    fun `refresh keeps the movie and uses isRefreshing instead of isLoading`() = runTest {
        val viewModel = createViewModel()
        details.emit(Resource.Success(movieDetails(42)))

        viewModel.onRefresh()
        details.emit(Resource.Loading)

        val state = viewModel.uiState.value
        assertTrue(state.isRefreshing)
        assertFalse(state.isLoading)
        assertEquals(movieDetails(42), state.movie)
        assertEquals(listOf(42, 42), requestedIds)

        details.emit(Resource.Success(movieDetails(42, title = "Updated")))
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertEquals("Updated", viewModel.uiState.value.movie?.title)
    }
}
