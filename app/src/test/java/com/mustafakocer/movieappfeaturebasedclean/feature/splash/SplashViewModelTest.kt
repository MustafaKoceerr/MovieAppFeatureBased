package com.mustafakocer.movieappfeaturebasedclean.feature.splash

import com.mustafakocer.core_domain.provider.SessionProvider
import com.mustafakocer.movieappfeaturebasedclean.feature.splash.presentation.viewmodel.SplashDestination
import com.mustafakocer.movieappfeaturebasedclean.feature.splash.presentation.viewmodel.SplashViewModel
import com.mustafakocer.movieappfeaturebasedclean.testutil.MainDispatcherRule
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(session: Flow<String?>) = SplashViewModel(
        object : SessionProvider {
            override fun observeSessionId(): Flow<String?> = session
        }
    )

    @Test
    fun `stays on the splash until the minimum time has passed`() = runTest {
        val viewModel = createViewModel(flowOf("session"))

        advanceTimeBy(1_499)
        assertNull(viewModel.uiState.value.destination)

        advanceUntilIdle()
        assertEquals(SplashDestination.Home, viewModel.uiState.value.destination)
    }

    @Test
    fun `stored session goes to home`() = runTest {
        val viewModel = createViewModel(flowOf("session-id"))

        advanceUntilIdle()

        assertEquals(SplashDestination.Home, viewModel.uiState.value.destination)
    }

    @Test
    fun `missing session goes to welcome`() = runTest {
        val viewModel = createViewModel(flowOf(null))

        advanceUntilIdle()

        assertEquals(SplashDestination.Welcome, viewModel.uiState.value.destination)
    }

    @Test
    fun `blank session goes to welcome`() = runTest {
        val viewModel = createViewModel(flowOf("  "))

        advanceUntilIdle()

        assertEquals(SplashDestination.Welcome, viewModel.uiState.value.destination)
    }

    @Test
    fun `a failing session lookup goes to welcome instead of crashing`() = runTest {
        val viewModel = createViewModel(flow { throw IOException("datastore unavailable") })

        advanceUntilIdle()

        assertEquals(SplashDestination.Welcome, viewModel.uiState.value.destination)
    }
}
