package com.mustafakocer.movieappfeaturebasedclean.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.mustafakocer.core_android.util.updateLocale
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.movieappfeaturebasedclean.navigation.AppNavHost
import com.mustafakocer.movieappfeaturebasedclean.presentation.viewmodel.MainViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * A Hilt Entry Point to access dependencies from non-Hilt-aware classes.
 *
 * Architectural Decision: This interface is crucial for applying the user's selected language
 * at the earliest possible moment in the app's lifecycle (`attachBaseContext`). The
 * `attachBaseContext` method runs before Hilt's standard dependency injection is available
 * for the Activity. This Entry Point provides a "backdoor" to access the Hilt dependency graph
 * and retrieve the `LanguageRepository` instance manually, allowing us to set the app's locale
 * before any UI is created.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface LanguageRepositoryEntryPoint {
    fun languageRepository(): LanguageRepository
}

/**
 * The main and only Activity in this Single-Activity Architecture application.
 *
 * This Activity serves as the host for the entire Jetpack Compose UI. Its primary responsibilities
 * are:
 * - Setting up the application's theme and language based on user preferences.
 * - Hosting the `AppNavHost`, which manages all navigation between composable screens.
 * - Responding to global configuration changes requested by feature modules (e.g., restarting
 *   the activity after a language change).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Overridden to set the application's locale based on the user's saved preference
     * *before* the Activity's UI is created. This is the correct lifecycle point to ensure
     * that all resources are loaded with the correct language configuration.
     */
    override fun attachBaseContext(newBase: Context) {
        // Use the Hilt Entry Point to access the LanguageRepository.
        val entryPoint = EntryPointAccessors.fromApplication(
            newBase.applicationContext,
            LanguageRepositoryEntryPoint::class.java
        )
        val repo = entryPoint.languageRepository()

        // Architectural Trade-off: `runBlocking` is used here as a pragmatic solution to bridge
        // the synchronous world of `attachBaseContext` with the asynchronous `Flow` from DataStore.
        // This blocks the main thread to get the initial language value. While blocking the main
        // thread is generally discouraged, it is acceptable in this specific, controlled scenario
        // because it's a one-time, extremely fast read from a local file during app startup.
        val languageCode = runBlocking {
            repo.languageFlow.first()
        }.code

        val locale = Locale(languageCode)
        // The custom extension function applies the new locale to the context.
        super.attachBaseContext(newBase.updateLocale(locale))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val navController = rememberNavController()

            // Reactively collect the current theme preference from the MainViewModel.
            // `collectAsStateWithLifecycle` ensures this collection is lifecycle-aware.
            val currentTheme by viewModel.themeState.collectAsStateWithLifecycle()

            // UI/UX Decision: `AnimatedContent` provides a smooth and polished transition when the
            // user switches the theme. Instead of an abrupt change, the content fades and slides,
            // enhancing the user experience.
            AnimatedContent(
                targetState = currentTheme,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                            slideInVertically(
                                animationSpec = tween(400, easing = FastOutSlowInEasing)
                            ) { it / 20 })
                        .togetherWith(
                            fadeOut(animationSpec = tween(200)) +
                                    slideOutVertically(animationSpec = tween(200)) { -it / 20 }
                        )
                },
                label = "ThemeTransition"
            ) { theme ->
                // The custom theme composable applies the appropriate colors and typography
                // based on the collected theme state.
                MovieDiscoveryTheme(theme = theme) {
                    // The AppNavHost is the container for the entire application's navigation graph.
                    // It uses its own default start destination (SplashFeatureGraph).
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}