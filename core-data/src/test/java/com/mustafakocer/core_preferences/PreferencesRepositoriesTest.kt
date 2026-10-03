package com.mustafakocer.core_preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import app.cash.turbine.test
import com.mustafakocer.core_preferences.datastore.PreferenceKeys
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.core_preferences.repository.SessionManager
import com.mustafakocer.core_preferences.repository.ThemeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Runs the preference repositories against an in-memory DataStore. */
class PreferencesRepositoriesTest {

    private val dataStore: DataStore<Preferences> = InMemoryPreferencesDataStore()

    // --- Language ---

    @Test
    fun `language defaults to the default language`() = runTest {
        val repository = LanguageRepository(dataStore)

        assertEquals(LanguagePreference.DEFAULT, repository.languageFlow.first())
    }

    @Test
    fun `saved language is read back and updates are emitted`() = runTest {
        val repository = LanguageRepository(dataStore)

        repository.setLanguage(LanguagePreference.TURKISH)
        assertEquals(LanguagePreference.TURKISH, repository.languageFlow.first())

        repository.setLanguage(LanguagePreference.GERMAN)
        assertEquals(LanguagePreference.GERMAN, repository.languageFlow.first())
    }

    @Test
    fun `an unknown stored language falls back to the default`() = runTest {
        dataStore.edit { it[PreferenceKeys.LANGUAGE_PREFERENCE] = "KLINGON" }

        assertEquals(LanguagePreference.DEFAULT, LanguageRepository(dataStore).languageFlow.first())
    }

    @Test
    fun `language flow emits again when the language changes`() = runTest {
        val repository = LanguageRepository(dataStore)

        repository.languageFlow.test {
            assertEquals(LanguagePreference.DEFAULT, awaitItem())

            repository.setLanguage(LanguagePreference.SPANISH)
            assertEquals(LanguagePreference.SPANISH, awaitItem())
        }
    }

    // --- Theme ---

    @Test
    fun `theme defaults to the default theme`() = runTest {
        assertEquals(ThemePreference.DEFAULT, ThemeRepository(dataStore).themeFlow.first())
    }

    @Test
    fun `saved theme is read back`() = runTest {
        val repository = ThemeRepository(dataStore)

        repository.setTheme(ThemePreference.DARK)

        assertEquals(ThemePreference.DARK, repository.themeFlow.first())
    }

    @Test
    fun `an unknown stored theme falls back to the default`() = runTest {
        dataStore.edit { it[PreferenceKeys.THEME_PREFERENCE] = "NEON" }

        assertEquals(ThemePreference.DEFAULT, ThemeRepository(dataStore).themeFlow.first())
    }

    // --- Session ---

    @Test
    fun `there is no session by default`() = runTest {
        assertNull(SessionManager(dataStore).sessionIdFlow.first())
    }

    @Test
    fun `session id can be saved and cleared`() = runTest {
        val sessionManager = SessionManager(dataStore)

        sessionManager.saveSessionId("session-123")
        assertEquals("session-123", sessionManager.sessionIdFlow.first())

        sessionManager.clearSessionId()
        assertNull(sessionManager.sessionIdFlow.first())
    }

    @Test
    fun `saving a session id does not touch the other preferences`() = runTest {
        LanguageRepository(dataStore).setLanguage(LanguagePreference.ITALIAN)
        ThemeRepository(dataStore).setTheme(ThemePreference.LIGHT)

        SessionManager(dataStore).saveSessionId("session-123")

        assertEquals(LanguagePreference.ITALIAN, LanguageRepository(dataStore).languageFlow.first())
        assertEquals(ThemePreference.LIGHT, ThemeRepository(dataStore).themeFlow.first())
    }
}
