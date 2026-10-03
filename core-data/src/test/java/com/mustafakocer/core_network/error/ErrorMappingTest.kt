package com.mustafakocer.core_network.error

import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.exception.toAppException
import java.io.IOException
import java.net.SocketTimeoutException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ErrorMappingTest {

    private fun httpException(code: Int) =
        HttpException(Response.error<Any>(code, "".toResponseBody(null)))

    @Test
    fun `socket timeout maps to Timeout, other IO errors to NoInternet`() {
        assertTrue(ErrorMapper.mapThrowableToAppException(SocketTimeoutException()) is AppException.Network.Timeout)
        assertTrue(ErrorMapper.mapThrowableToAppException(IOException()) is AppException.Network.NoInternet)
        assertTrue(SocketTimeoutException().toAppException() is AppException.Network.Timeout)
        assertTrue(IOException().toAppException() is AppException.Network.NoInternet)
    }

    @Test
    fun `http exceptions map by status code`() {
        assertTrue(ErrorMapper.mapThrowableToAppException(httpException(401)) is AppException.Api.Unauthorized)
        assertTrue(ErrorMapper.mapThrowableToAppException(httpException(404)) is AppException.Api.NotFound)

        val serverError = ErrorMapper.mapThrowableToAppException(httpException(503))
        assertTrue(serverError is AppException.Api.ServerError)
        assertEquals(503, (serverError as AppException.Api.ServerError).httpCode)

        assertTrue(ErrorMapper.mapThrowableToAppException(httpException(418)) is AppException.Unknown)
    }

    @Test
    fun `http error responses map by status code`() {
        fun error(code: Int) = Response.error<Any>(code, "".toResponseBody(null))

        assertTrue(ErrorMapper.mapHttpErrorResponseToAppException(error(401)) is AppException.Api.Unauthorized)
        assertTrue(ErrorMapper.mapHttpErrorResponseToAppException(error(404)) is AppException.Api.NotFound)
        assertTrue(ErrorMapper.mapHttpErrorResponseToAppException(error(500)) is AppException.Api.ServerError)
        assertTrue(ErrorMapper.mapHttpErrorResponseToAppException(error(400)) is AppException.Unknown)
    }

    @Test
    fun `existing AppException is passed through and unknown errors are wrapped`() {
        val existing = AppException.Data.EmptyResponse

        assertSame(existing, ErrorMapper.mapThrowableToAppException(existing))
        assertSame(existing, existing.toAppException())
        assertTrue(IllegalStateException("boom").toAppException() is AppException.Unknown)
    }
}
