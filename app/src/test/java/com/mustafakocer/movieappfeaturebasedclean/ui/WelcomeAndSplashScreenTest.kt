package com.mustafakocer.movieappfeaturebasedclean.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.movieappfeaturebasedclean.R
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.welcome.presentation.screen.WelcomeScreen
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.welcome.presentation.viewmodel.WelcomeUiState
import com.mustafakocer.movieappfeaturebasedclean.feature.splash.presentation.screen.SplashScreen
import com.mustafakocer.movieappfeaturebasedclean.testutil.string
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h900dp")
class WelcomeAndSplashScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private var loginClicks = 0
    private var guestClicks = 0

    private fun showWelcome(state: WelcomeUiState) {
        composeRule.setContent {
            MovieDiscoveryTheme {
                WelcomeScreen(
                    state = state,
                    onLoginClick = { loginClicks++ },
                    onGuestClick = { guestClicks++ },
                )
            }
        }
    }

    // --- Welcome ---

    @Test
    fun `welcome shows the title and both entry options`() {
        showWelcome(WelcomeUiState())

        composeRule.onNodeWithText(composeRule.string(R.string.welcome_title)).assertIsDisplayed()
        composeRule.onNodeWithText(composeRule.string(R.string.login_with_tmdb)).assertIsDisplayed()
        composeRule.onNodeWithText(composeRule.string(R.string.continue_as_guest)).assertIsDisplayed()
    }

    @Test
    fun `login and guest buttons report their clicks`() {
        showWelcome(WelcomeUiState())

        composeRule.onNodeWithText(composeRule.string(R.string.login_with_tmdb)).performClick()
        composeRule.onNodeWithText(composeRule.string(R.string.continue_as_guest)).performClick()

        assertEquals(1, loginClicks)
        assertEquals(1, guestClicks)
    }

    @Test
    fun `buttons are enabled when nothing is loading`() {
        showWelcome(WelcomeUiState(isLoading = false))

        composeRule.onNodeWithText(composeRule.string(R.string.login_with_tmdb)).assertIsEnabled()
        composeRule.onNodeWithText(composeRule.string(R.string.continue_as_guest)).assertIsEnabled()
    }

    @Test
    fun `while loading the guest button is disabled and the login label is replaced by a spinner`() {
        showWelcome(WelcomeUiState(isLoading = true))

        composeRule.onNodeWithText(composeRule.string(R.string.continue_as_guest)).assertIsNotEnabled()
        // The login button shows a progress indicator instead of its label.
        composeRule.onAllNodesWithText(composeRule.string(R.string.login_with_tmdb)).assertCountEquals(0)
    }

    // --- Splash ---

    @Test
    fun `splash shows the logo and the app name`() {
        composeRule.setContent { MovieDiscoveryTheme { SplashScreen() } }
        composeRule.mainClock.autoAdvance = false
        composeRule.mainClock.advanceTimeBy(2_000)

        composeRule.onNodeWithContentDescription(composeRule.string(R.string.app_logo)).assertIsDisplayed()
        composeRule.onNodeWithText(composeRule.string(R.string.app_name_one)).assertIsDisplayed()
    }
}
