package com.mustafakocer.movieappfeaturebasedclean.feature.search

import androidx.paging.PagingData
import app.cash.turbine.test
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import com.mustafakocer.movieappfeaturebasedclean.feature.search.data.repository.SearchRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.search.domain.model.SearchQuery
import com.mustafakocer.movieappfeaturebasedclean.feature.search.presentation.viewmodel.SearchViewModel
import com.mustafakocer.movieappfeaturebasedclean.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /** The (debounced) query flow the ViewModel hands to the repository. */
    private lateinit var queryFlowPassedToRepository: Flow<String>

    private val repository: SearchRepository =
        Mockito.mock(SearchRepository::class.java) { invocation ->
            queryFlowPassedToRepository = invocation.getArgument(0)
            flowOf(PagingData.empty<MovieListItem>())
        }

    private fun createViewModel() = SearchViewModel(repository)

    @Test
    fun `query changes update the search query state`() = runTest {
        val viewModel = createViewModel()

        viewModel.onQueryChange("matrix")

        assertEquals("matrix", viewModel.searchQuery.value)
    }

    @Test
    fun `clear resets the search query`() = runTest {
        val viewModel = createViewModel()
        viewModel.onQueryChange("matrix")

        viewModel.onClearSearch()

        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `repository only receives the last query after the debounce time`() = runTest {
        val viewModel = createViewModel()

        queryFlowPassedToRepository.test {
            viewModel.onQueryChange("ma")
            viewModel.onQueryChange("mat")
            viewModel.onQueryChange("matrix")

            advanceTimeBy(499)
            expectNoEvents()

            advanceTimeBy(2)
            assertEquals("matrix", awaitItem())
        }
    }

    @Test
    fun `search query validity follows the length rules`() {
        assertFalse(SearchQuery("ab").isValid)
        assertFalse(SearchQuery("  ab  ").isValid)
        assertTrue(SearchQuery("abc").isValid)
        assertTrue(SearchQuery("  abc  ").isValid)
        assertFalse(SearchQuery("a".repeat(SearchQuery.MAX_LENGTH + 1)).isValid)
        assertEquals("matrix", SearchQuery("  matrix ").cleanQuery)
    }
}
