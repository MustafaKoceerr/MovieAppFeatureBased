package com.mustafakocer.movieappfeaturebasedclean.feature.home

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.movieappfeaturebasedclean.feature.home.domain.usecase.GetHomeScreenDataUseCase
import com.mustafakocer.movieappfeaturebasedclean.feature.home.presentation.viewmodel.HomeViewModel
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieCategory
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import com.mustafakocer.movieappfeaturebasedclean.testutil.MainDispatcherRule
import com.mustafakocer.movieappfeaturebasedclean.testutil.movieListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val homeData = MutableSharedFlow<Resource<Map<MovieCategory, List<MovieListItem>>>>(
        extraBufferCapacity = 8
    )
    private val requestedRefreshFlags = mutableListOf<Boolean>()

    private val useCase: GetHomeScreenDataUseCase =
        Mockito.mock(GetHomeScreenDataUseCase::class.java) { invocation ->
            requestedRefreshFlags += invocation.getArgument<Boolean>(0)
            homeData as Flow<*>
        }

    private val content = mapOf(MovieCategory.POPULAR to listOf(movieListItem(1), movieListItem(2)))

    private fun createViewModel() = HomeViewModel(useCase)

    @Test
    fun `loading with no content shows full screen loading`() = runTest {
        val viewModel = createViewModel()

        homeData.emit(Resource.Loading)

        val state = viewModel.uiState.value
        assertTrue(state.isLoading)
        assertTrue(state.showFullScreenLoading)
    }

    @Test
    fun `success exposes categories and clears loading and error`() = runTest {
        val viewModel = createViewModel()

        homeData.emit(Resource.Loading)
        homeData.emit(Resource.Success(content))

        val state = viewModel.uiState.value
        assertEquals(content, state.categories)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `error with no content shows full screen error`() = runTest {
        val viewModel = createViewModel()

        homeData.emit(Resource.Error(AppException.Network.NoInternet()))

        val state = viewModel.uiState.value
        assertTrue(state.showFullScreenError)
        assertFalse(state.isLoading)
    }

    @Test
    fun `error with cached content keeps content and is not full screen`() = runTest {
        val viewModel = createViewModel()
        homeData.emit(Resource.Success(content))

        homeData.emit(Resource.Error(AppException.Network.NoInternet()))

        val state = viewModel.uiState.value
        assertEquals(content, state.categories)
        assertFalse(state.showFullScreenError)
    }

    @Test
    fun `loading with cached content does not flash the skeleton`() = runTest {
        val viewModel = createViewModel()
        homeData.emit(Resource.Success(content))

        homeData.emit(Resource.Loading)

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `refresh requests fresh data and toggles isRefreshing`() = runTest {
        val viewModel = createViewModel()
        homeData.emit(Resource.Success(content))

        viewModel.onRefresh()
        assertTrue(viewModel.uiState.value.isRefreshing)

        homeData.emit(Resource.Success(content))
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertEquals(listOf(false, true), requestedRefreshFlags)
    }
}
