package com.mustafakocer.movieappfeaturebasedclean

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.core_android.util.updateLocale
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.movieappfeaturebasedclean.navigation.AppNavHost
import com.mustafakocer.movieappfeaturebasedclean.presentation.viewmodel.MainViewModel
import com.mustafakocer.navigation_contracts.navigation.AuthFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.MoviesFeatureGraph
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

@EntryPoint
@InstallIn(SingletonComponent::class)
interface LanguageRepositoryEntryPoint {
    fun languageRepository(): LanguageRepository
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Bu metod, Activity'nin Context'i oluşturulurken çağrılır.
     * Dil ayarını, herhangi bir UI bileşeni çizilmeden önce yapmak için
     * en doğru yer burasıdır.
     */
    override fun attachBaseContext(newBase: Context) {
        // EntryPoint'i kullanarak Hilt grafiğinden LanguageRepository'yi alıyoruz.
        val entryPoint = EntryPointAccessors.fromApplication(
            newBase.applicationContext,
            LanguageRepositoryEntryPoint::class.java
        )
        val repo = entryPoint.languageRepository()

        // runBlocking burada kaçınılmazdır çünkü bu metodun senkron olması gerekir.
        val languageCode = runBlocking{
            repo.languageFlow.first().code
        }

        val locale = Locale(languageCode)

        // Yeni dil ayarıyla güncellenmiş Context'i Activity'e ata.
        super.attachBaseContext(newBase.updateLocale(locale))
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("MainActivity", "OnCreate'e girdi: ")
        setContent {
            // 1. ViewModel'i ve NavController'ı en dış katmanda, bir kez oluştur.
            // Bunlar artık tema değişikliğinden etkilenmeyecek.
            val viewModel: MainViewModel = hiltViewModel()
            val navController = rememberNavController()
            val startDestination = determineStartDestination()

            val currentTheme by viewModel.themeState.collectAsStateWithLifecycle()

            AnimatedContent(
                targetState = currentTheme,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                            slideInVertically(
                                animationSpec = tween(
                                    400,
                                    easing = FastOutSlowInEasing
                                )
                            ) { it / 20 })
                        .togetherWith(
                            fadeOut(animationSpec = tween(200)) +
                                    slideOutVertically(animationSpec = tween(200)) { -it / 20 }
                        )
                },
                label = "ThemeTransition"
            ) { theme ->
                MovieDiscoveryTheme(theme = theme) {
                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }

    /**
     * Determine which graph to start with based on user state
     *
     * TODO: Implement proper auth state checking
     * - Check if user is logged in (SharedPreferences/DataStore)
     * - Check if user completed onboarding
     * - Return appropriate start destination
     */
    private fun determineStartDestination(): Any {
        // For now, always start with Movies since auth is not implemented
        // Later: Check user auth state and return AuthGraph or MoviesGraph

        return AuthFeatureGraph
        return MoviesFeatureGraph

        // Future implementation:
        // return if (isUserLoggedIn()) MoviesGraph else AuthGraph
    }
}

// TODO: Implement when auth feature is ready
// private fun isUserLoggedIn(): Boolean {
//     // Check auth state from repository/datastore
//     return false
// }



