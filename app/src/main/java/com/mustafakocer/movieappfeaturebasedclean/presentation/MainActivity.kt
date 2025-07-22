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

    override fun attachBaseContext(newBase: Context) {
        val entryPoint = EntryPointAccessors.fromApplication(
            newBase.applicationContext,
            LanguageRepositoryEntryPoint::class.java
        )
        val repo = entryPoint.languageRepository()

        val languageCode = runBlocking {
            repo.languageFlow.first().code
        }
        val locale = Locale(languageCode)
        super.attachBaseContext(newBase.updateLocale(locale))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val navController = rememberNavController()
            val currentTheme by viewModel.themeState.collectAsStateWithLifecycle()

            // AnimatedContent ve MovieDiscoveryTheme yapısı aynı kalıyor.
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
                MovieDiscoveryTheme(theme = theme) {
                    // AppNavHost'a artık startDestination vermiyoruz.
                    // Kendi varsayılanını (SplashFeatureGraph) kullanacak.
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}