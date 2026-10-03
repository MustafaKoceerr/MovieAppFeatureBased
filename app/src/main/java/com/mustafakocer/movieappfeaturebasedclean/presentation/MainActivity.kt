package com.mustafakocer.movieappfeaturebasedclean.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.movieappfeaturebasedclean.navigation.AppNavHost
import com.mustafakocer.movieappfeaturebasedclean.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main and only Activity in this Single-Activity Architecture application.
 *
 * It hosts the entire Jetpack Compose UI: it applies the user's theme and hosts the `AppNavHost`
 * that manages all navigation between composable screens.
 *
 * It extends [AppCompatActivity] because AppCompat's per-app locale support (used for the in-app
 * language setting, see `applyAppLanguage`) needs it on Android 12 and below.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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