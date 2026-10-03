package com.mustafakocer.core_network.util

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class SafeApiCallTest {

    private fun httpError(code: Int): Response<String> =
        Response.error(code, "".toResponseBody(null))

    @Test
    fun `successful response emits loading and then the body`() = runTest {
        val emissions = safeApiCall { Response.success("body") }.toList()

        assertEquals(listOf(Resource.Loading, Resource.Success("body")), emissions)
    }

    @Test
    fun `successful response without a body is an empty response error`() = runTest {
        val emissions = safeApiCall { Response.success<String>(null) }.toList()

        assertEquals(
            listOf(Resource.Loading, Resource.Error(AppException.Data.EmptyResponse)),
            emissions
        )
    }

    @Test
    fun `http error responses are mapped by status code`() = runTest {
        val notFound = safeApiCall { httpError(404) }.toList().last() as Resource.Error
        val unauthorized = safeApiCall { httpError(401) }.toList().last() as Resource.Error
        val serverError = safeApiCall { httpError(500) }.toList().last() as Resource.Error

        assertTrue(notFound.exception is AppException.Api.NotFound)
        assertTrue(unauthorized.exception is AppException.Api.Unauthorized)
        assertTrue(serverError.exception is AppException.Api.ServerError)
    }

    @Test
    fun `thrown exceptions become error resources`() = runTest {
        val timeout = safeApiCall<String> { throw SocketTimeoutException() }.toList()
        val offline = safeApiCall<String> { throw IOException() }.toList()
        val unexpected = safeApiCall<String> { throw IllegalStateException("boom") }.toList()

        assertEquals(Resource.Loading, timeout.first())
        assertTrue((timeout.last() as Resource.Error).exception is AppException.Network.Timeout)
        assertTrue((offline.last() as Resource.Error).exception is AppException.Network.NoInternet)
        assertTrue((unexpected.last() as Resource.Error).exception is AppException.Unknown)
    }

    @Test
    fun `the api call only runs when the flow is collected`() = runTest {
        var called = false
        val flow = safeApiCall {
            called = true
            Response.success("body")
        }

        assertFalse(called)
        flow.toList()
        assertTrue(called)
    }
}
