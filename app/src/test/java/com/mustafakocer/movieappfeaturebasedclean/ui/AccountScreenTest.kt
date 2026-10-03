package com.mustafakocer.movieappfeaturebasedclean.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.movieappfeaturebasedclean.R
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.account.presentation.screen.AccountScreen
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.account.presentation.viewmodel.AccountUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AccountScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun string(id: Int) = composeRule.activity.getString(id)

    private var loginClicks = 0
    private var logoutClicks = 0
    private var backClicks = 0

    private fun show(state: AccountUiState) {
        composeRule.setContent {
            MovieDiscoveryTheme {
                AccountScreen(
                    state = state,
                    onBackClick = { backClicks++ },
                    onLoginClick = { loginClicks++ },
                    onLogoutClick = { logoutClicks++ },
                )
            }
        }
    }

    @Test
    fun `guest sees the guest card and can log in`() {
        show(AccountUiState(isLoggedIn = false))

        composeRule.onNodeWithText(string(R.string.guest_card_title)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.login_button)).performClick()

        assertEquals(1, loginClicks)
        assertEquals(0, logoutClicks)
    }

    @Test
    fun `logged in user sees logout instead of login`() {
        show(AccountUiState(isLoggedIn = true))

        composeRule.onNodeWithText(string(R.string.logout_button)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.login_button)).assertDoesNotExist()

        composeRule.onNodeWithText(string(R.string.logout_button)).performClick()

        assertEquals(1, logoutClicks)
        assertEquals(0, loginClicks)
    }

    @Test
    fun `guest card shows the localized title and subtitle`() {
        show(AccountUiState(isLoggedIn = false))

        composeRule.onNodeWithText(string(R.string.guest_card_title)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.guest_card_subtitle)).assertIsDisplayed()
    }

    @Test
    fun `logged in profile shows greeting welcome text and avatar`() {
        show(AccountUiState(isLoggedIn = true))

        composeRule.onNodeWithText(string(R.string.account_greeting)).assertIsDisplayed()
        composeRule.onNodeWithText(string(R.string.account_welcome_back)).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(string(R.string.account_avatar_description)).assertIsDisplayed()
    }

    @Test
    fun `back button is wired`() {
        show(AccountUiState())

        composeRule.onNodeWithContentDescription(string(R.string.back_button_desc)).performClick()

        assertEquals(1, backClicks)
    }
}
