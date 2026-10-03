package com.mustafakocer.movieappfeaturebasedclean.feature.auth

import com.mustafakocer.movieappfeaturebasedclean.feature.auth.account.presentation.viewmodel.AccountViewModel
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.repository.AuthRepository
import com.mustafakocer.movieappfeaturebasedclean.testutil.MainDispatcherRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class AccountViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val session = MutableStateFlow<String?>(null)
    private var logoutCalls = 0

    private val authRepository: AuthRepository =
        Mockito.mock(AuthRepository::class.java) { invocation ->
            when (invocation.method.name) {
                "observeSessionId" -> session
                "logout" -> {
                    logoutCalls++
                    Unit
                }
                else -> null
            }
        }

    private fun createViewModel() = AccountViewModel(authRepository)

    @Test
    fun `guest when there is no session`() = runTest {
        val viewModel = createViewModel()

        assertFalse(viewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun `logged in follows the stored session`() = runTest {
        val viewModel = createViewModel()

        session.value = "session-id"
        assertTrue(viewModel.uiState.value.isLoggedIn)

        session.value = ""
        assertFalse(viewModel.uiState.value.isLoggedIn)

        session.value = "another-session"
        assertTrue(viewModel.uiState.value.isLoggedIn)

        session.value = null
        assertFalse(viewModel.uiState.value.isLoggedIn)
    }

    @Test
    fun `logout calls the repository and reports completion`() = runTest {
        session.value = "session-id"
        val viewModel = createViewModel()
        assertFalse(viewModel.uiState.value.loggedOut)

        viewModel.onLogoutClick()

        assertEquals(1, logoutCalls)
        assertTrue(viewModel.uiState.value.loggedOut)
    }

    @Test
    fun `session changes after logout started no longer update the state`() = runTest {
        session.value = "session-id"
        val viewModel = createViewModel()
        assertTrue(viewModel.uiState.value.isLoggedIn)

        viewModel.onLogoutClick()
        session.value = null

        // The guest state must not flash while the screen is navigating away.
        assertTrue(viewModel.uiState.value.isLoggedIn)
    }
}
