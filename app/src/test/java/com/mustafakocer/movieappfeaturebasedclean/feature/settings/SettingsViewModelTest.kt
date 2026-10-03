package com.mustafakocer.movieappfeaturebasedclean.feature.settings

import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.core_preferences.repository.ThemeRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.settings.presentation.viewmodel.SettingsViewModel
import com.mustafakocer.movieappfeaturebasedclean.testutil.MainDispatcherRule
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val themeFlow = MutableStateFlow(ThemePreference.SYSTEM)
    private val languageFlow = MutableStateFlow(LanguagePreference.ENGLISH)
    private val savedThemes = mutableListOf<ThemePreference>()
    private val savedLanguages = mutableListOf<LanguagePreference>()
    private var failSaving = false

    private val themeRepository: ThemeRepository =
        Mockito.mock(ThemeRepository::class.java) { invocation ->
            when (invocation.method.name) {
                "getThemeFlow" -> themeFlow
                "setTheme" -> {
                    if (failSaving) throw IOException("disk full")
                    savedThemes += invocation.getArgument<ThemePreference>(0)
                    themeFlow.value = invocation.getArgument(0)
                }
                else -> null
            }
        }

    private val languageRepository: LanguageRepository =
        Mockito.mock(LanguageRepository::class.java) { invocation ->
            when (invocation.method.name) {
                "getLanguageFlow" -> languageFlow
                "setLanguage" -> {
                    if (failSaving) throw IOException("disk full")
                    savedLanguages += invocation.getArgument<LanguagePreference>(0)
                    languageFlow.value = invocation.getArgument(0)
                }
                else -> null
            }
        }

    private fun createViewModel() = SettingsViewModel(themeRepository, languageRepository)

    @Test
    fun `state reflects the stored theme and language`() = runTest {
        themeFlow.value = ThemePreference.DARK
        languageFlow.value = LanguagePreference.TURKISH

        val state = createViewModel().uiState.value

        assertEquals(ThemePreference.DARK, state.currentTheme)
        assertEquals(LanguagePreference.TURKISH, state.currentLanguage)
    }

    @Test
    fun `selecting a new language saves it and asks the route to apply it`() = runTest {
        val viewModel = createViewModel()

        viewModel.onLanguageSelected(LanguagePreference.GERMAN)

        assertEquals(listOf(LanguagePreference.GERMAN), savedLanguages)
        val state = viewModel.uiState.value
        assertEquals(LanguagePreference.GERMAN, state.languageToApply)
        assertFalse(state.isSaving)

        viewModel.onLanguageApplied()
        assertNull(viewModel.uiState.value.languageToApply)
    }

    @Test
    fun `selecting the current language does nothing`() = runTest {
        val viewModel = createViewModel()

        viewModel.onLanguageSelected(LanguagePreference.ENGLISH)

        assertEquals(emptyList<LanguagePreference>(), savedLanguages)
        assertNull(viewModel.uiState.value.languageToApply)
    }

    @Test
    fun `a failed language save shows an error and does not request a language change`() = runTest {
        val viewModel = createViewModel()
        failSaving = true

        viewModel.onLanguageSelected(LanguagePreference.FRENCH)

        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertFalse(state.isSaving)
        assertNull(state.languageToApply)

        viewModel.onErrorShown()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `selecting a theme saves it without requesting a language change`() = runTest {
        val viewModel = createViewModel()

        viewModel.onThemeSelected(ThemePreference.DARK)

        assertEquals(listOf(ThemePreference.DARK), savedThemes)
        assertEquals(ThemePreference.DARK, viewModel.uiState.value.currentTheme)
        assertNull(viewModel.uiState.value.languageToApply)
    }
}
