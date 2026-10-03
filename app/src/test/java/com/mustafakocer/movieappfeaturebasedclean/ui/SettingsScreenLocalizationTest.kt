package com.mustafakocer.movieappfeaturebasedclean.ui

import androidx.activity.ComponentActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.movieappfeaturebasedclean.feature.settings.presentation.screen.SettingsScreen
import com.mustafakocer.movieappfeaturebasedclean.feature.settings.presentation.viewmodel.SettingsUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The Settings labels used to be hard-coded English ("Light", "Dark", "System", "Language"). These
 * tests render the screen in other locales and check the real translated texts.
 */
@RunWith(RobolectricTestRunner::class)
class SettingsScreenLocalizationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun show(currentLanguage: LanguagePreference) {
        composeRule.setContent {
            MovieDiscoveryTheme {
                SettingsScreen(
                    state = SettingsUiState(currentLanguage = currentLanguage),
                    snackbarHostState = remember { SnackbarHostState() },
                    onBackClick = {},
                    onThemeSelected = {},
                    onLanguageSelected = {},
                    onErrorShown = {},
                )
            }
        }
    }

    private fun assertNoEnglishLabels() {
        listOf("Light", "Dark", "System", "Language").forEach { english ->
            composeRule.onAllNodesWithText(english).assertCountEquals(0)
        }
    }

    @Test
    @Config(qualifiers = "de-w400dp-h1200dp")
    fun `german labels`() {
        show(LanguagePreference.GERMAN)

        composeRule.onNodeWithText("Hell").assertIsDisplayed()
        composeRule.onNodeWithText("Dunkel").assertIsDisplayed()
        composeRule.onNodeWithText("System").assertIsDisplayed() // "System" is also German
        composeRule.onNodeWithText("Sprache").assertIsDisplayed()
        composeRule.onAllNodesWithText("Light").assertCountEquals(0)
        composeRule.onAllNodesWithText("Dark").assertCountEquals(0)
        composeRule.onAllNodesWithText("Language").assertCountEquals(0)
    }

    @Test
    @Config(qualifiers = "tr-w400dp-h1200dp")
    fun `turkish labels`() {
        show(LanguagePreference.TURKISH)

        composeRule.onNodeWithText("Açık Tema").assertIsDisplayed()
        composeRule.onNodeWithText("Koyu Tema").assertIsDisplayed()
        composeRule.onNodeWithText("Sistem Ayarları").assertIsDisplayed()
        composeRule.onNodeWithText("Dil").assertIsDisplayed()
        assertNoEnglishLabels()
    }

    @Test
    @Config(qualifiers = "ru-w400dp-h1200dp")
    fun `russian labels`() {
        show(LanguagePreference.RUSSIAN)

        composeRule.onNodeWithText("Светлая").assertIsDisplayed()
        composeRule.onNodeWithText("Тёмная").assertIsDisplayed()
        composeRule.onNodeWithText("Системная").assertIsDisplayed()
        composeRule.onNodeWithText("Язык").assertIsDisplayed()
        assertNoEnglishLabels()
    }

    @Test
    @Config(qualifiers = "w400dp-h1200dp")
    fun `english labels are the default`() {
        show(LanguagePreference.ENGLISH)

        composeRule.onNodeWithText("Light").assertIsDisplayed()
        composeRule.onNodeWithText("Dark").assertIsDisplayed()
        composeRule.onNodeWithText("System").assertIsDisplayed()
        composeRule.onNodeWithText("Language").assertIsDisplayed()
    }
}
