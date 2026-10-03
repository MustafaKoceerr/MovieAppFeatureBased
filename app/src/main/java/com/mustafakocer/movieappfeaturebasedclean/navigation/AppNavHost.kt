package com.mustafakocer.movieappfeaturebasedclean.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.navigation.authNavGraph
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.navigation.moviesNavGraph
import com.mustafakocer.movieappfeaturebasedclean.feature.splash.navigation.splashNavGraph
import com.mustafakocer.movieappfeaturebasedclean.navigation.SplashFeatureGraph

/**
 * The main navigation host for the entire application.
 *
 * This composable is the central orchestrator for the app's navigation. It sets up the `NavHost`
 * and includes the encapsulated navigation graphs from all the different feature modules.
 *
 * @param navController The top-level [NavHostController] that manages the navigation state.
 * @param modifier The modifier to be applied to the NavHost container.
 * @param startDestination The route of the initial feature graph to be displayed when the app starts.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: SplashFeatureGraph = SplashFeatureGraph,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Architectural Decision: The `:app` module is completely decoupled from the internal
        // screens of the feature modules (it doesn't know about `HomeRoute`, `SearchRoute`, etc.).
        // Instead, it calls a single extension function (e.g., `moviesNavGraph`) that each feature
        // module exposes. This is the core principle of modular navigation, keeping the app-level
        // graph clean and making features plug-and-play.

        // The splash screen feature graph is the entry point of the application.
        splashNavGraph(navController = navController)

        // The main movies feature graph.
        moviesNavGraph(navController = navController)

        // The authentication feature graph.
        authNavGraph(
            navController = navController
        )

        // When a new feature (e.g., `:feature-profile`) is added in the future,
        // its navigation graph would be included here in the same manner.
        // e.g., profileNavGraph(navController)
    }
}