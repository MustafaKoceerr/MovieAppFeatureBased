package com.mustafakocer.core_preferences

import com.mustafakocer.core_network.interceptor.LanguageInterceptor
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.provider.LanguageProvider
import com.mustafakocer.core_preferences.repository.LanguageRepository
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.mockito.Mockito

class LanguageProviderTest {

    private val languageRepository = LanguageRepository(InMemoryPreferencesDataStore())
    private val provider = LanguageProvider(languageRepository)

    // --- LanguageProvider ---

    @Test
    fun `the default language is used when nothing is stored yet`() = runTest {
        // Regression: the provider used to return an empty string until its background collector
        // had received the first value.
        assertEquals(LanguagePreference.DEFAULT.apiParam, provider.getLanguageParam())
        assertNotEquals("", provider.getLanguageParam())
    }

    @Test
    fun `the stored language is returned`() = runTest {
        languageRepository.setLanguage(LanguagePreference.TURKISH)

        assertEquals("tr-TR", provider.getLanguageParam())
    }

    @Test
    fun `a language change is visible immediately`() = runTest {
        // Regression: a separately cached value could still be the old language right after a change.
        languageRepository.setLanguage(LanguagePreference.TURKISH)
        assertEquals("tr-TR", provider.getLanguageParam())

        languageRepository.setLanguage(LanguagePreference.GERMAN)
        assertEquals("de-DE", provider.getLanguageParam())

        languageRepository.setLanguage(LanguagePreference.ENGLISH)
        assertEquals("en-US", provider.getLanguageParam())
    }

    @Test
    fun `every supported language has a non-empty api parameter`() = runTest {
        LanguagePreference.entries.forEach { language ->
            languageRepository.setLanguage(language)

            assertEquals(language.apiParam, provider.getLanguageParam())
            assertNotEquals("", provider.getLanguageParam())
        }
    }

    // --- LanguageInterceptor ---

    private fun intercept(url: String = "https://api.themoviedb.org/3/movie/popular?page=1"): Request {
        val original = Request.Builder().url(url).build()
        var sent: Request? = null
        val chain = Mockito.mock(Interceptor.Chain::class.java) { invocation ->
            when (invocation.method.name) {
                "request" -> original
                "proceed" -> {
                    sent = invocation.getArgument<Request>(0)
                    Response.Builder()
                        .request(sent!!)
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .build()
                }
                else -> null
            }
        }

        LanguageInterceptor(provider).intercept(chain)

        return sent!!
    }

    @Test
    fun `the interceptor appends the stored language and keeps existing parameters`() = runTest {
        languageRepository.setLanguage(LanguagePreference.FRENCH)

        val request = intercept()

        assertEquals("fr-FR", request.url.queryParameter("language"))
        assertEquals("1", request.url.queryParameter("page"))
    }

    @Test
    fun `the first request already carries a language`() = runTest {
        val request = intercept()

        assertEquals(LanguagePreference.DEFAULT.apiParam, request.url.queryParameter("language"))
    }

    @Test
    fun `the interceptor uses the new language right after a change`() = runTest {
        languageRepository.setLanguage(LanguagePreference.SPANISH)
        assertEquals("es-ES", intercept().url.queryParameter("language"))

        languageRepository.setLanguage(LanguagePreference.ITALIAN)
        assertEquals("it-IT", intercept().url.queryParameter("language"))
    }
}
