package com.mustafakocer.movieappfeaturebasedclean

import android.os.Bundle
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
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.movieappfeaturebasedclean.navigation.AppNavHost
import com.mustafakocer.navigation_contracts.destinations.MoviesGraph
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.movieappfeaturebasedclean.presentation.viewmodel.MainViewModel

/**
 * Main Activity with integrated navigation
 *
 * RESPONSIBILITIES:
 * ✅ Setup navigation controller
 * ✅ Determine start destination (auth vs main app)
 * ✅ Apply app theme
 * ✅ Handle system UI (edge-to-edge)
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 1. ViewModel'i ve NavController'ı en dış katmanda, bir kez oluştur.
            // Bunlar artık tema değişikliğinden etkilenmeyecek.
            val viewModel: MainViewModel = hiltViewModel()
            val navController = rememberNavController()
            val startDestination = determineStartDestination()

            // 2. Tema state'ini topla. Bu değiştiğinde, sadece AnimatedContent yeniden çalışacak.
            val currentTheme by viewModel.themeState.collectAsStateWithLifecycle()

            // 3. AnimatedContent, sadece tema ile ilgili olan kısmı sarmalasın.
            AnimatedContent(
                targetState = currentTheme,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                            slideInVertically(animationSpec = tween(400, easing = FastOutSlowInEasing)) { it / 20 })
                        .togetherWith(
                            fadeOut(animationSpec = tween(200)) +
                                    slideOutVertically(animationSpec = tween(200)) { -it / 20 }
                        )
                },
                label = "ThemeTransition"
            ) { theme ->
                // 4. MovieDiscoveryTheme, her animasyon karesi için doğru temayı uygular.
                MovieDiscoveryTheme(theme = theme) {
                    // 5. AppContent adında yeni bir Composable oluşturduk.
                    // Bu, NavHost'u ve Scaffold'u içerir ve tema değişikliğinden etkilenmez.
                    // Sadece temanın kendisi değiştiği için yeniden çizilir.
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

        return MoviesGraph

        // Future implementation:
        // return if (isUserLoggedIn()) MoviesGraph else AuthGraph
    }
}

// TODO: Implement when auth feature is ready
// private fun isUserLoggedIn(): Boolean {
//     // Check auth state from repository/datastore
//     return false
// }
