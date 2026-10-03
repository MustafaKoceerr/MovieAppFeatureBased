package com.mustafakocer.movieappfeaturebasedclean.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.movieappfeaturebasedclean.R
import com.mustafakocer.movieappfeaturebasedclean.feature.settings.presentation.screen.SettingsScreen
import com.mustafakocer.movieappfeaturebasedclean.feature.settings.presentation.viewmodel.SettingsUiState
import com.mustafakocer.movieappfeaturebasedclean.testutil.string
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.mustafakocer.core_ui.R as CoreUiR

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h1200dp")
class SettingsScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private var backClicks = 0
    private var errorsShown = 0
    private val selectedThemes = mutableListOf<ThemePreference>()
    private val selectedLanguages = mutableListOf<LanguagePreference>()

    private fun show(state: SettingsUiState) {
        composeRule.setContent {
            MovieDiscoveryTheme {
                SettingsScreen(
                    state = state,
                    snackbarHostState = remember { SnackbarHostState() },
                    onBackClick = { backClicks++ },
                    onThemeSelected = { selectedThemes += it },
                    onLanguageSelected = { selectedLanguages += it },
                    onErrorShown = { errorsShown++ },
                )
            }
        }
    }

    // --- theme ---

    @Test
    fun `the current theme is selected and the others are not`() {
        show(SettingsUiState(currentTheme = ThemePreference.DARK))

        composeRule.onNodeWithText(ThemePreference.DARK.displayName).assertIsSelected()
        composeRule.onNodeWithText(ThemePreference.LIGHT.displayName).assertIsNotSelected()
        composeRule.onNodeWithText(ThemePreference.SYSTEM.displayName).assertIsNotSelected()
    }

    @Test
    fun `choosing a theme reports it`() {
        show(SettingsUiState(currentTheme = ThemePreference.SYSTEM))

        composeRule.onNodeWithText(ThemePreference.LIGHT.displayName).performClick()

        assertEquals(listOf(ThemePreference.LIGHT), selectedThemes)
    }

    @Test
    fun `theme options are disabled while a preference is being saved`() {
        show(SettingsUiState(isSaving = true))

        composeRule.onNodeWithText(ThemePreference.LIGHT.displayName).assertIsNotEnabled()
        composeRule.onNodeWithText(ThemePreference.DARK.displayName).assertIsNotEnabled()
    }

    @Test
    fun `theme options are enabled when nothing is being saved`() {
        show(SettingsUiState(isSaving = false))

        composeRule.onNodeWithText(ThemePreference.LIGHT.displayName).assertIsEnabled()
    }

    // --- language ---

    @Test
    fun `the current language is shown in the language field`() {
        show(SettingsUiState(currentLanguage = LanguagePreference.TURKISH))

        composeRule.onNodeWithText(LanguagePreference.TURKISH.displayName).assertIsDisplayed()
    }

    @Test
    fun `choosing a language from the dropdown reports it`() {
        show(SettingsUiState(currentLanguage = LanguagePreference.ENGLISH))

        composeRule.onNodeWithText(LanguagePreference.ENGLISH.displayName).performClick()
        composeRule.onNodeWithText(LanguagePreference.GERMAN.displayName).performClick()

        assertEquals(listOf(LanguagePreference.GERMAN), selectedLanguages)
    }

    // --- navigation and errors ---

    @Test
    fun `back button is wired`() {
        show(SettingsUiState())

        composeRule.onNodeWithContentDescription(composeRule.string(R.string.back)).performClick()

        assertEquals(1, backClicks)
    }

    @Test
    fun `an error is shown once in a snackbar and then reported as shown`() {
        val title = composeRule.string(CoreUiR.string.error_title_no_internet)
        show(SettingsUiState(error = AppException.Network.NoInternet()))

        composeRule.onNodeWithText(title, substring = true).assertIsDisplayed()
        assertEquals(0, errorsShown)

        // The snackbar auto-dismisses after its short duration, then the screen reports it as shown.
        composeRule.mainClock.advanceTimeBy(10_000)
        composeRule.waitForIdle()

        assertEquals(1, errorsShown)
    }
}
