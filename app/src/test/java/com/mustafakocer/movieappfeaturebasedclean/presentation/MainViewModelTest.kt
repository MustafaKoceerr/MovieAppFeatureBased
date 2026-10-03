package com.mustafakocer.movieappfeaturebasedclean.presentation

import app.cash.turbine.test
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_preferences.repository.ThemeRepository
import com.mustafakocer.movieappfeaturebasedclean.presentation.viewmodel.MainViewModel
import com.mustafakocer.movieappfeaturebasedclean.testutil.MainDispatcherRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val themeFlow = MutableStateFlow(ThemePreference.LIGHT)

    private val themeRepository: ThemeRepository =
        Mockito.mock(ThemeRepository::class.java) { invocation ->
            if (invocation.method.name == "getThemeFlow") themeFlow else null
        }

    private fun createViewModel() = MainViewModel(themeRepository)

    @Test
    fun `theme state follows the stored theme preference`() = runTest {
        val viewModel = createViewModel()

        viewModel.themeState.test {
            assertEquals(ThemePreference.LIGHT, expectMostRecentItem())

            themeFlow.value = ThemePreference.DARK
            assertEquals(ThemePreference.DARK, awaitItem())

            themeFlow.value = ThemePreference.SYSTEM
            assertEquals(ThemePreference.SYSTEM, awaitItem())
        }
    }

    @Test
    fun `theme state starts as system before the preference is loaded`() = runTest {
        val viewModel = createViewModel()

        assertEquals(ThemePreference.SYSTEM, viewModel.themeState.value)
    }
}
