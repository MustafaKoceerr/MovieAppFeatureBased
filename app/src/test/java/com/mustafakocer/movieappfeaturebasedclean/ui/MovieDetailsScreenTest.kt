package com.mustafakocer.movieappfeaturebasedclean.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.movieappfeaturebasedclean.R
import com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.screen.MovieDetailsScreen
import com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.viewmodel.MovieDetailsUiState
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.Genre
import com.mustafakocer.movieappfeaturebasedclean.testutil.movieDetails
import com.mustafakocer.movieappfeaturebasedclean.testutil.string
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.mustafakocer.core_ui.R as CoreUiR

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h1200dp")
class MovieDetailsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private var backClicks = 0
    private var refreshes = 0
    private val sharedTexts = mutableListOf<String>()

    private val movie = movieDetails(id = 1, title = "The Matrix").copy(
        overview = "A hacker discovers the truth about his reality.",
        tagline = "Welcome to the real world",
        genres = listOf(Genre(1, "Action"), Genre(2, "Sci-Fi")),
    )

    private fun show(state: MovieDetailsUiState) {
        composeRule.setContent {
            MovieDiscoveryTheme {
                MovieDetailsScreen(
                    state = state,
                    snackbarHostState = remember { SnackbarHostState() },
                    onBackClick = { backClicks++ },
                    onRefresh = { refreshes++ },
                    onShareClick = { sharedTexts += it },
                )
            }
        }
    }

    @Test
    fun `content shows the title overview and genres`() {
        show(MovieDetailsUiState(movie = movie))

        composeRule.onAllNodesWithText("The Matrix")[0].assertIsDisplayed()
        composeRule.onNodeWithText("A hacker discovers the truth about his reality.").assertIsDisplayed()
        composeRule.onNodeWithText("Action").assertIsDisplayed()
        composeRule.onNodeWithText("Sci-Fi").assertIsDisplayed()
        // The tagline is rendered inside quotation marks.
        composeRule.onNodeWithText("\"Welcome to the real world\"").assertIsDisplayed()
    }

    @Test
    fun `a movie without genres does not show the genres section`() {
        show(MovieDetailsUiState(movie = movie.copy(genres = emptyList())))

        composeRule.onAllNodesWithText(composeRule.string(R.string.genres)).assertCountEquals(0)
    }

    @Test
    fun `back button is wired`() {
        show(MovieDetailsUiState(movie = movie))

        composeRule.onNodeWithContentDescription(composeRule.string(R.string.navigate_back)).performClick()

        assertEquals(1, backClicks)
    }

    @Test
    fun `share button passes the formatted movie text`() {
        show(MovieDetailsUiState(movie = movie))

        composeRule.onNodeWithContentDescription(composeRule.string(R.string.share_movie)).performClick()

        assertEquals(1, sharedTexts.size)
        assertTrue(sharedTexts.single().contains("The Matrix"))
        assertTrue(sharedTexts.single().contains("A hacker discovers the truth about his reality."))
    }

    @Test
    fun `there is no share button while no movie is loaded`() {
        show(MovieDetailsUiState(isLoading = true))

        composeRule.onAllNodesWithContentDescription(composeRule.string(R.string.share_movie)).assertCountEquals(0)
    }

    @Test
    fun `loading shows no content and no error`() {
        show(MovieDetailsUiState(isLoading = true))
        composeRule.waitForIdle()

        composeRule.onAllNodesWithText(composeRule.string(R.string.overview)).assertCountEquals(0)
        composeRule.onAllNodesWithText(composeRule.string(CoreUiR.string.error_retry)).assertCountEquals(0)
    }

    @Test
    fun `an error without a movie shows the error screen and retry refreshes`() {
        show(MovieDetailsUiState(error = AppException.Api.NotFound()))

        composeRule.onNodeWithText(composeRule.string(CoreUiR.string.error_title_not_found)).assertIsDisplayed()
        composeRule.onNodeWithText(composeRule.string(CoreUiR.string.error_retry)).performClick()

        assertEquals(1, refreshes)
    }

    @Test
    fun `refreshing keeps the movie visible and shows the refresh indicator`() {
        show(MovieDetailsUiState(movie = movie, isRefreshing = true))

        composeRule.onNodeWithText("A hacker discovers the truth about his reality.").assertIsDisplayed()
        composeRule.onNodeWithText(composeRule.string(R.string.refreshing)).assertIsDisplayed()
    }
}
