package com.mustafakocer.movieappfeaturebasedclean.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mustafakocer.feature_auth.navigation.authNavGraph
import com.mustafakocer.feature_movies.navigation.moviesNavGraph
import com.mustafakocer.feature_splash.navigation.splashNavGraph
import com.mustafakocer.navigation_contracts.navigation.SplashFeatureGraph

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
    // Architectural Decision: `LocalActivity.current` is used to get a reference to the host
    // Activity. This is necessary for implementing "hoisted actions" – actions that a feature
    // module requests but cannot perform itself, such as restarting the activity.
    val activity = LocalActivity.current

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
        moviesNavGraph(
            navController = navController,
            // Hoisted Action Implementation: The `moviesNavGraph` requires a function to handle
            // language changes. Here in the `:app` module, we provide the concrete implementation
            // for that action by calling `activity.recreate()`. This keeps the `feature-movies`
            // module independent of the Android Activity framework, a key goal of Clean Architecture.
            onLanguageChanged = { activity?.recreate() }
        )

        // The authentication feature graph.
        authNavGraph(
            navController = navController
        )

        // When a new feature (e.g., `:feature-profile`) is added in the future,
        // its navigation graph would be included here in the same manner.
        // e.g., profileNavGraph(navController)
    }
}