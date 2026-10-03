package com.mustafakocer.movieappfeaturebasedclean.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.error.toErrorInfo
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.account.presentation.screen.AccountScreen
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.account.presentation.viewmodel.AccountUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The Account texts and the error screen buttons used to be hard-coded English. These tests render
 * them in other locales and check the real translated texts.
 */
@RunWith(RobolectricTestRunner::class)
class AccountAndErrorLocalizationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private var retries = 0
    private var goBacks = 0

    private fun showError() {
        composeRule.setContent {
            MovieDiscoveryTheme {
                ErrorScreen(
                    error = AppException.Network.NoInternet().toErrorInfo(),
                    onRetry = { retries++ },
                    onNavigateBack = { goBacks++ },
                )
            }
        }
    }

    private fun showAccount(loggedIn: Boolean) {
        composeRule.setContent {
            MovieDiscoveryTheme {
                AccountScreen(
                    state = AccountUiState(isLoggedIn = loggedIn),
                    onBackClick = {},
                    onLoginClick = {},
                    onLogoutClick = {},
                )
            }
        }
    }

    private fun assertNoEnglishAccountTexts() {
        listOf("Hello, User!", "Welcome back").forEach { composeRule.onAllNodesWithText(it).assertCountEquals(0) }
    }

    // --- error screen buttons ---

    @Test
    @Config(qualifiers = "w400dp-h900dp")
    fun `english error buttons and their callbacks`() {
        showError()

        composeRule.onNodeWithText("Go Back").performClick()
        composeRule.onNodeWithText("Try Again").performClick()

        assertEquals(1, goBacks)
        assertEquals(1, retries)
    }

    @Test
    @Config(qualifiers = "de-w400dp-h900dp")
    fun `german error buttons`() {
        showError()

        composeRule.onNodeWithText("Zurück").assertIsDisplayed()
        composeRule.onNodeWithText("Erneut versuchen").assertIsDisplayed()
        composeRule.onAllNodesWithText("Go Back").assertCountEquals(0)
        composeRule.onAllNodesWithText("Try Again").assertCountEquals(0)
    }

    @Test
    @Config(qualifiers = "tr-w400dp-h900dp")
    fun `turkish error buttons`() {
        showError()

        composeRule.onNodeWithText("Geri Dön").assertIsDisplayed()
        composeRule.onNodeWithText("Tekrar Dene").assertIsDisplayed()
        composeRule.onAllNodesWithText("Go Back").assertCountEquals(0)
        composeRule.onAllNodesWithText("Try Again").assertCountEquals(0)
    }

    @Test
    @Config(qualifiers = "ru-w400dp-h900dp")
    fun `russian error buttons`() {
        showError()

        composeRule.onNodeWithText("Назад").assertIsDisplayed()
        composeRule.onNodeWithText("Попробовать снова").assertIsDisplayed()
        composeRule.onAllNodesWithText("Go Back").assertCountEquals(0)
        composeRule.onAllNodesWithText("Try Again").assertCountEquals(0)
    }

    // --- account texts ---

    @Test
    @Config(qualifiers = "de-w400dp-h900dp")
    fun `german account texts`() {
        showAccount(loggedIn = false)
        composeRule.onNodeWithText("Noch kein Konto?").assertIsDisplayed()
        composeRule.onAllNodesWithText("Don't have an account?").assertCountEquals(0)
    }

    @Test
    @Config(qualifiers = "de-w400dp-h900dp")
    fun `german logged in profile`() {
        showAccount(loggedIn = true)

        composeRule.onNodeWithText("Hallo, Benutzer!").assertIsDisplayed()
        composeRule.onNodeWithText("Willkommen zurück").assertIsDisplayed()
        assertNoEnglishAccountTexts()
    }

    @Test
    @Config(qualifiers = "tr-w400dp-h900dp")
    fun `turkish account texts`() {
        showAccount(loggedIn = false)
        composeRule.onNodeWithText("Hesabınız yok mu?").assertIsDisplayed()
        composeRule.onAllNodesWithText("Don't have an account?").assertCountEquals(0)
    }

    @Test
    @Config(qualifiers = "tr-w400dp-h900dp")
    fun `turkish logged in profile`() {
        showAccount(loggedIn = true)

        composeRule.onNodeWithText("Merhaba, Kullanıcı!").assertIsDisplayed()
        composeRule.onNodeWithText("Tekrar hoş geldiniz").assertIsDisplayed()
        assertNoEnglishAccountTexts()
    }

    @Test
    @Config(qualifiers = "fr-w400dp-h900dp")
    fun `french account texts with an apostrophe`() {
        showAccount(loggedIn = false)

        composeRule.onNodeWithText("Vous n'avez pas de compte ?").assertIsDisplayed()
    }
}
