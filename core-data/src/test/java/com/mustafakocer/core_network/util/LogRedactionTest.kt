package com.mustafakocer.core_network.util

import org.junit.Assert.assertEquals
import org.junit.Test

class LogRedactionTest {

    @Test
    fun `masks api key in a query string and keeps other parameters`() {
        val line = "--> GET https://api.themoviedb.org/3/movie/popular?language=en&api_key=SECRET123&page=1"

        assertEquals(
            "--> GET https://api.themoviedb.org/3/movie/popular?language=en&api_key=***&page=1",
            line.redactSecrets()
        )
    }

    @Test
    fun `masks session id and request token in query strings`() {
        assertEquals("a?session_id=***", "a?session_id=abc123".redactSecrets())
        assertEquals("a?request_token=***&x=1", "a?request_token=tok456&x=1".redactSecrets())
    }

    @Test
    fun `masks secrets inside json bodies`() {
        val body = """{"success":true,"session_id":"abc123","request_token": "tok456"}"""

        assertEquals(
            """{"success":true,"session_id":"***","request_token": "***"}""",
            body.redactSecrets()
        )
    }

    @Test
    fun `leaves lines without secrets untouched`() {
        val line = """<-- 200 OK https://api.themoviedb.org/3/movie/1 {"title":"Matrix"}"""

        assertEquals(line, line.redactSecrets())
    }
}
