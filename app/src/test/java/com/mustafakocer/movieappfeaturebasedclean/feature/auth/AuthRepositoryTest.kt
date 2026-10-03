package com.mustafakocer.movieappfeaturebasedclean.feature.auth

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_preferences.repository.SessionManager
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.api.AuthApiService
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.model.DeleteSessionRequestDto
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.model.DeleteSessionResponseDto
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.model.RequestTokenDto
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.model.SessionDto
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.model.SessionRequestDto
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.shared.data.repository.AuthRepository
import com.mustafakocer.movieappfeaturebasedclean.testutil.InMemoryPreferencesDataStore
import com.mustafakocer.movieappfeaturebasedclean.testutil.httpError
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response

class AuthRepositoryTest {

    private val sessionManager = SessionManager(InMemoryPreferencesDataStore())

    private var requestTokenResponse: Response<RequestTokenDto> =
        Response.success(RequestTokenDto(success = true, expiresAt = "2030-01-01", requestToken = "request-token"))
    private var sessionResponse: Response<SessionDto> =
        Response.success(SessionDto(success = true, sessionId = "session-id"))
    private var deleteSessionFails = false
    private val sentSessionRequests = mutableListOf<String>()
    private val deletedSessionIds = mutableListOf<String>()

    private val api: AuthApiService = Mockito.mock(AuthApiService::class.java) { invocation ->
        when (invocation.method.name) {
            "createRequestToken" -> requestTokenResponse
            "createSession" -> {
                sentSessionRequests += invocation.getArgument<SessionRequestDto>(0).requestToken
                sessionResponse
            }
            "deleteSession" -> {
                deletedSessionIds += invocation.getArgument<DeleteSessionRequestDto>(0).sessionId
                if (deleteSessionFails) throw IOException("offline")
                Response.success(DeleteSessionResponseDto(success = true))
            }
            else -> null
        }
    }

    private val repository = AuthRepository(api, sessionManager)

    // --- createRequestToken ---

    @Test
    fun `request token is emitted as a plain string`() = runTest {
        val emissions = repository.createRequestToken().toList()

        assertEquals(listOf(Resource.Loading, Resource.Success("request-token")), emissions)
    }

    @Test
    fun `request token failure is mapped to an error`() = runTest {
        requestTokenResponse = httpError(401)

        val last = repository.createRequestToken().toList().last()

        assertTrue((last as Resource.Error).exception is AppException.Api.Unauthorized)
    }

    // --- createSession ---

    @Test
    fun `created session is returned and stored locally`() = runTest {
        val emissions = repository.createSession("approved-token").toList()

        assertEquals(listOf(Resource.Loading, Resource.Success("session-id")), emissions)
        assertEquals(listOf("approved-token"), sentSessionRequests)
        assertEquals("session-id", sessionManager.sessionIdFlow.first())
    }

    @Test
    fun `a response without a session id is a parse error and nothing is stored`() = runTest {
        sessionResponse = Response.success(SessionDto(success = true, sessionId = null))

        val last = repository.createSession("approved-token").toList().last()

        assertTrue((last as Resource.Error).exception is AppException.Data.Parse)
        assertNull(sessionManager.sessionIdFlow.first())
    }

    @Test
    fun `a failed session request stores nothing`() = runTest {
        sessionResponse = httpError(401)

        val last = repository.createSession("denied-token").toList().last()

        assertTrue((last as Resource.Error).exception is AppException.Api.Unauthorized)
        assertNull(sessionManager.sessionIdFlow.first())
    }

    // --- session observation ---

    @Test
    fun `observeSessionId reflects the stored session`() = runTest {
        assertNull(repository.observeSessionId().first())

        sessionManager.saveSessionId("stored-session")

        assertEquals("stored-session", repository.observeSessionId().first())
    }

    // --- logout ---

    @Test
    fun `logout clears the local session and deletes it remotely`() = runTest {
        sessionManager.saveSessionId("session-id")

        repository.logout()

        assertNull(sessionManager.sessionIdFlow.first())
        assertEquals(listOf("session-id"), deletedSessionIds)
    }

    @Test
    fun `logout without a session does not call the api`() = runTest {
        repository.logout()

        assertTrue(deletedSessionIds.isEmpty())
    }

    @Test
    fun `logout still succeeds locally when the remote deletion fails`() = runTest {
        sessionManager.saveSessionId("session-id")
        deleteSessionFails = true

        repository.logout()

        assertNull(sessionManager.sessionIdFlow.first())
        assertEquals(listOf("session-id"), deletedSessionIds)
    }
}
