package com.mustafakocer.movieappfeaturebasedclean.feature.auth

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.repository.AuthRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.util.AuthCallbackHandler
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.welcome.presentation.viewmodel.WelcomeViewModel
import com.mustafakocer.movieappfeaturebasedclean.testutil.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class WelcomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val requestToken = MutableSharedFlow<Resource<String>>(extraBufferCapacity = 8)
    private val sessionCreation = MutableSharedFlow<Resource<String>>(extraBufferCapacity = 8)
    private val tokensUsedForSession = mutableListOf<String>()

    private val authRepository: AuthRepository =
        Mockito.mock(AuthRepository::class.java) { invocation ->
            when (invocation.method.name) {
                "createRequestToken" -> requestToken as Flow<*>
                "createSession" -> {
                    tokensUsedForSession += invocation.getArgument<String>(0)
                    sessionCreation as Flow<*>
                }
                else -> null
            }
        }

    private val callbackHandler = AuthCallbackHandler()

    private fun createViewModel() = WelcomeViewModel(authRepository, callbackHandler)

    @Test
    fun `login click shows loading and then exposes the TMDB approval url`() = runTest {
        val viewModel = createViewModel()

        viewModel.onLoginClick()
        requestToken.emit(Resource.Loading)
        assertTrue(viewModel.uiState.value.isLoading)

        requestToken.emit(Resource.Success("request-token"))

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(
            "https://www.themoviedb.org/authenticate/request-token" +
                "?redirect_to=movieapp://auth/callback",
            state.loginUrl
        )
    }

    @Test
    fun `the login url can be consumed`() = runTest {
        val viewModel = createViewModel()
        viewModel.onLoginClick()
        requestToken.emit(Resource.Success("request-token"))

        viewModel.onLoginUrlHandled()

        assertNull(viewModel.uiState.value.loginUrl)
    }

    @Test
    fun `a failed request token shows an error and no url`() = runTest {
        val viewModel = createViewModel()
        viewModel.onLoginClick()

        requestToken.emit(Resource.Error(AppException.Network.NoInternet()))

        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertFalse(state.isLoading)
        assertNull(state.loginUrl)
    }

    @Test
    fun `an approved token from the callback creates a session and logs in`() = runTest {
        val viewModel = createViewModel()

        callbackHandler.onNewTokenReceived("approved-token")
        sessionCreation.emit(Resource.Loading)
        assertTrue(viewModel.uiState.value.isLoading)

        sessionCreation.emit(Resource.Success("session-id"))

        assertEquals(listOf("approved-token"), tokensUsedForSession)
        val state = viewModel.uiState.value
        assertTrue(state.loggedIn)
        assertFalse(state.isLoading)
    }

    @Test
    fun `a token delivered before the screen existed is still handled`() = runTest {
        callbackHandler.onNewTokenReceived("early-token")

        val viewModel = createViewModel()
        sessionCreation.emit(Resource.Success("session-id"))

        assertEquals(listOf("early-token"), tokensUsedForSession)
        assertTrue(viewModel.uiState.value.loggedIn)
    }

    @Test
    fun `a failed session creation shows an error and does not log in`() = runTest {
        val viewModel = createViewModel()
        callbackHandler.onNewTokenReceived("approved-token")

        sessionCreation.emit(Resource.Error(AppException.Api.Unauthorized()))

        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertFalse(state.loggedIn)
        assertFalse(state.isLoading)
    }
}
