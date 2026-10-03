package com.mustafakocer.movieappfeaturebasedclean.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.movieappfeaturebasedclean.R
import com.mustafakocer.movieappfeaturebasedclean.feature.home.presentation.screen.HomeScreen
import com.mustafakocer.movieappfeaturebasedclean.feature.home.presentation.viewmodel.HomeUiState
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieCategory
import com.mustafakocer.movieappfeaturebasedclean.testutil.movieListItem
import com.mustafakocer.movieappfeaturebasedclean.testutil.string
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.mustafakocer.core_ui.R as CoreUiR

@RunWith(RobolectricTestRunner::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private var refreshes = 0
    private var searchClicks = 0
    private var settingsClicks = 0
    private var accountClicks = 0
    private val clickedMovies = mutableListOf<Int>()
    private val viewAllCategories = mutableListOf<MovieCategory>()

    private val content = HomeUiState(
        categories = mapOf(
            MovieCategory.POPULAR to listOf(movieListItem(1, "Popular One"), movieListItem(2, "Popular Two")),
            MovieCategory.TOP_RATED to listOf(movieListItem(3, "Top Rated One")),
        )
    )

    private fun show(state: HomeUiState) {
        composeRule.setContent {
            MovieDiscoveryTheme {
                HomeScreen(
                    state = state,
                    snackbarHostState = remember { SnackbarHostState() },
                    onRefresh = { refreshes++ },
                    onMovieClick = { clickedMovies += it },
                    onViewAllClick = { viewAllCategories += it },
                    onSearchClick = { searchClicks++ },
                    onSettingsClick = { settingsClicks++ },
                    onAccountClick = { accountClicks++ },
                )
            }
        }
    }

    @Test
    fun `content shows the categories and their movies`() {
        show(content)

        composeRule.onNodeWithText(composeRule.string(R.string.category_title_popular)).assertIsDisplayed()
        composeRule.onNodeWithText("Popular One").assertIsDisplayed()
        composeRule.onNodeWithText("Popular Two").assertIsDisplayed()
        composeRule.onNodeWithText(composeRule.string(R.string.category_title_top_rated)).assertIsDisplayed()
    }

    @Test
    fun `tapping a movie reports its id`() {
        show(content)

        composeRule.onNodeWithText("Popular Two").performClick()

        assertEquals(listOf(2), clickedMovies)
    }

    @Test
    fun `view all reports the category of its section`() {
        show(content)

        // The first section is POPULAR, the second one TOP_RATED.
        composeRule.onAllNodesWithText(composeRule.string(R.string.view_all))[1].performClick()

        assertEquals(listOf(MovieCategory.TOP_RATED), viewAllCategories)
    }

    @Test
    fun `search bar and top bar actions are wired`() {
        show(content)

        composeRule.onNodeWithText(composeRule.string(R.string.search_movies)).performClick()
        composeRule.onNodeWithContentDescription(composeRule.string(R.string.settings)).performClick()
        composeRule.onNodeWithContentDescription(composeRule.string(R.string.account)).performClick()

        assertEquals(1, searchClicks)
        assertEquals(1, settingsClicks)
        assertEquals(1, accountClicks)
    }

    @Test
    fun `loading without content shows the skeleton and no content`() {
        show(HomeUiState(isLoading = true))
        composeRule.waitForIdle()

        composeRule.onNodeWithText(composeRule.string(R.string.app_name)).assertIsDisplayed()
        composeRule.onAllNodesWithText(composeRule.string(R.string.view_all)).assertCountEquals(0)
        composeRule.onAllNodesWithText("Try Again").assertCountEquals(0)
    }

    @Test
    fun `an error without content shows the error screen and retry refreshes`() {
        show(HomeUiState(error = AppException.Network.NoInternet()))

        composeRule.onNodeWithText(composeRule.string(CoreUiR.string.error_title_no_internet)).assertIsDisplayed()
        composeRule.onNodeWithText("Try Again").performClick()

        assertEquals(1, refreshes)
    }

    @Test
    fun `an error with cached content keeps showing the content`() {
        show(content.copy(error = AppException.Network.NoInternet()))

        composeRule.onNodeWithText("Popular One").assertIsDisplayed()
        composeRule.onAllNodesWithText("Try Again").assertCountEquals(0)
    }
}
