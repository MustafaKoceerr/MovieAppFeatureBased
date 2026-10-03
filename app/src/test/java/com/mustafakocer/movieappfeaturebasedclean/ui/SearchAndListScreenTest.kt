package com.mustafakocer.movieappfeaturebasedclean.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.paging.LoadState
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.movieappfeaturebasedclean.R
import com.mustafakocer.movieappfeaturebasedclean.feature.list.presentation.screen.MovieListScreen
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieCategory
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import com.mustafakocer.movieappfeaturebasedclean.feature.search.presentation.screen.SearchScreen
import com.mustafakocer.movieappfeaturebasedclean.testutil.movieListItem
import com.mustafakocer.movieappfeaturebasedclean.testutil.pagingItemsOf
import com.mustafakocer.movieappfeaturebasedclean.testutil.string
import com.mustafakocer.movieappfeaturebasedclean.testutil.waitUntilTextExists
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.mustafakocer.core_ui.R as CoreUiR

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h1200dp")
class SearchAndListScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val movies = listOf(
        movieListItem(1, "The Matrix"),
        movieListItem(2, "The Matrix Reloaded"),
        movieListItem(3, "The Matrix Revolutions"),
    )

    private var backClicks = 0
    private var submits = 0
    private var clears = 0
    private val queries = mutableListOf<String>()
    private val clickedMovies = mutableListOf<Int>()

    // --- Search ---

    private fun showSearch(query: String, results: List<MovieListItem> = movies) {
        composeRule.setContent {
            MovieDiscoveryTheme {
                SearchScreen(
                    searchQuery = query,
                    searchResults = pagingItemsOf(results),
                    onQueryChange = { queries += it },
                    onClearSearch = { clears++ },
                    onSearchSubmitted = { submits++ },
                    onBackClick = { backClicks++ },
                    onMovieClick = { clickedMovies += it },
                )
            }
        }
    }

    @Test
    fun `a query that is too short shows the search prompt instead of results`() {
        showSearch(query = "ma")

        composeRule.onNodeWithText(composeRule.string(R.string.start_searching)).assertIsDisplayed()
        composeRule.onAllNodesWithText("The Matrix").assertCountEquals(0)
    }

    @Test
    fun `a long enough query shows the results`() {
        showSearch(query = "matrix")
        composeRule.waitUntilTextExists("The Matrix")

        composeRule.onNodeWithText("The Matrix").assertIsDisplayed()
        composeRule.onNodeWithText("The Matrix Reloaded").assertIsDisplayed()
        composeRule.onAllNodesWithText(composeRule.string(R.string.start_searching)).assertCountEquals(0)
    }

    @Test
    fun `tapping a result reports its id`() {
        showSearch(query = "matrix")
        composeRule.waitUntilTextExists("The Matrix Reloaded")

        composeRule.onNodeWithText("The Matrix Reloaded").performClick()

        assertEquals(listOf(2), clickedMovies)
    }

    @Test
    fun `typing reports the entered text`() {
        showSearch(query = "")

        composeRule.onNode(hasSetTextAction()).performTextInput("matrix")

        assertEquals(listOf("matrix"), queries)
    }

    @Test
    fun `the clear button only exists when there is a query and reports a clear`() {
        showSearch(query = "")
        composeRule.onAllNodesWithContentDescription(composeRule.string(R.string.clear_search)).assertCountEquals(0)
    }

    @Test
    fun `clearing a non-empty query is reported`() {
        showSearch(query = "matrix")

        composeRule.onNodeWithContentDescription(composeRule.string(R.string.clear_search)).performClick()

        assertEquals(1, clears)
    }

    @Test
    fun `the keyboard done action submits the search`() {
        showSearch(query = "matrix")

        composeRule.onNode(hasSetTextAction()).performImeAction()

        assertEquals(1, submits)
    }

    @Test
    fun `search back button is wired`() {
        showSearch(query = "")

        composeRule.onNodeWithContentDescription(composeRule.string(R.string.back)).performClick()

        assertEquals(1, backClicks)
    }

    // --- Movie list ---

    private fun showList(
        category: MovieCategory? = MovieCategory.POPULAR,
        items: List<MovieListItem> = movies,
        refresh: LoadState = LoadState.NotLoading(endOfPaginationReached = true),
    ) {
        composeRule.setContent {
            MovieDiscoveryTheme {
                MovieListScreen(
                    category = category,
                    movies = pagingItemsOf(items, refresh),
                    onBackClick = { backClicks++ },
                    onMovieClick = { clickedMovies += it },
                )
            }
        }
    }

    @Test
    fun `list shows the category title and its movies`() {
        showList(category = MovieCategory.TOP_RATED)
        composeRule.waitUntilTextExists("The Matrix")

        composeRule.onNodeWithText(composeRule.string(R.string.category_title_top_rated)).assertIsDisplayed()
        composeRule.onNodeWithText("The Matrix Reloaded").assertIsDisplayed()
        composeRule.onNodeWithText("The Matrix Revolutions").assertIsDisplayed()
    }

    @Test
    fun `tapping a movie in the list reports its id`() {
        showList()
        composeRule.waitUntilTextExists("The Matrix Revolutions")

        composeRule.onNodeWithText("The Matrix Revolutions").performClick()

        assertEquals(listOf(3), clickedMovies)
    }

    @Test
    fun `an empty list shows the no movies message`() {
        showList(items = emptyList())
        composeRule.waitUntilTextExists(composeRule.string(R.string.no_movies_found))

        composeRule.onNodeWithText(composeRule.string(R.string.no_movies_found)).assertIsDisplayed()
    }

    @Test
    fun `a failed first load shows the error screen`() {
        showList(items = emptyList(), refresh = LoadState.Error(AppException.Network.NoInternet()))
        composeRule.waitUntilTextExists(composeRule.string(CoreUiR.string.error_title_no_internet))

        composeRule.onNodeWithText(composeRule.string(CoreUiR.string.error_title_no_internet)).assertIsDisplayed()
        composeRule.onNodeWithText("Try Again").assertIsDisplayed()
    }

    @Test
    fun `list back button is wired`() {
        showList()

        composeRule.onNodeWithContentDescription(composeRule.string(R.string.back)).performClick()

        assertEquals(1, backClicks)
    }
}
