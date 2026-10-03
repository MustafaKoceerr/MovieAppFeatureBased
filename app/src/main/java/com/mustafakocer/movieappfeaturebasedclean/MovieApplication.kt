package com.mustafakocer.movieappfeaturebasedclean

import android.app.Application
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.movieappfeaturebasedclean.util.applyAppLanguage
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltAndroidApp
class MovieApplication : Application() {

    @Inject
    lateinit var languageRepository: LanguageRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        syncAppLanguage()
    }

    /**
     * The saved language (DataStore) is the source of truth for both the UI and the API language.
     * It is applied asynchronously instead of blocking the main thread at startup. After the first
     * run the language is already applied by AppCompat/the system, so this is normally a no-op;
     * on the very first run it switches the UI from the device language to the saved default.
     */
    private fun syncAppLanguage() {
        applicationScope.launch {
            applyAppLanguage(languageRepository.languageFlow.first())
        }
    }
}
