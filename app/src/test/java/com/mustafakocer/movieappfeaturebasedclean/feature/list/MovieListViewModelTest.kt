package com.mustafakocer.movieappfeaturebasedclean.feature.list

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.mustafakocer.movieappfeaturebasedclean.feature.list.data.repository.MovieListRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.list.presentation.viewmodel.MovieListViewModel
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieCategory
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import com.mustafakocer.movieappfeaturebasedclean.navigation.MovieListScreen
import com.mustafakocer.movieappfeaturebasedclean.testutil.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class MovieListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val requestedCategories = mutableListOf<MovieCategory>()

    private val repository: MovieListRepository =
        Mockito.mock(MovieListRepository::class.java) { invocation ->
            requestedCategories += invocation.getArgument<MovieCategory>(0)
            flowOf(PagingData.empty<MovieListItem>())
        }

    private fun createViewModel(endpoint: String) = MovieListViewModel(
        movieListRepository = repository,
        savedStateHandle = SavedStateHandle(mapOf(MovieListScreen::categoryEndpoint.name to endpoint)),
    )

    @Test
    fun `known endpoint resolves the category and loads its movies`() = runTest {
        val viewModel = createViewModel("top_rated")

        assertEquals(MovieCategory.TOP_RATED, viewModel.category)
        assertNotNull(viewModel.movies.first())
        assertEquals(listOf(MovieCategory.TOP_RATED), requestedCategories)
    }

    @Test
    fun `every category endpoint maps to its category`() = runTest {
        MovieCategory.entries.forEach { category ->
            assertEquals(category, createViewModel(category.apiEndpoint).category)
        }
    }

    @Test
    fun `unknown endpoint has no category and never queries the repository`() = runTest {
        val viewModel = createViewModel("not_a_category")

        assertNull(viewModel.category)
        assertTrue(viewModel.movies.toList().isEmpty())
        assertTrue(requestedCategories.isEmpty())
    }
}
