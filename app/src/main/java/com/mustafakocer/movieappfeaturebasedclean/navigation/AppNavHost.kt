package com.mustafakocer.movieappfeaturebasedclean.navigation

import androidx.navigation.NavHostController
import com.mustafakocer.navigation_contracts.*
import com.mustafakocer.navigation_contracts.AuthNavActions
import com.mustafakocer.navigation_contracts.MoviesNavActions
import com.mustafakocer.navigation_contracts.destinations.*
import com.mustafakocer.navigation_contracts.destinations.AuthGraph
import com.mustafakocer.navigation_contracts.destinations.HomeDestination
import com.mustafakocer.navigation_contracts.destinations.MoviesGraph
import com.mustafakocer.navigation_contracts.destinations.RegisterDestination
import com.mustafakocer.navigation_contracts.destinations.SearchDestination
import com.mustafakocer.navigation_contracts.destinations.SettingsDestination
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.mustafakocer.feature_movies.details.presentation.screen.MovieDetailsRoute
import com.mustafakocer.feature_movies.list.presentation.screen.MovieListRoute

/**
 * Main navigation host for the entire application
 *
 * ARCHITECTURE:
 * ✅ Type-safe navigation with @Serializable
 * ✅ Decoupled features via action interfaces
 * ✅ Centralized navigation logic
 * ✅ Clear navigation graphs separation
 */

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Any, // AuthGraph or MoviesGraph
    modifier: Modifier = Modifier,
) {
    // Create navigation actions implementation
    val navActions = remember(navController) {
        AppNavActionsImpl(navController)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // ==================== AUTH NAVIGATION GRAPH ====================

        navigation<AuthGraph>(
            startDestination = LoginDestination
        ) {
            composable<LoginDestination> {
                // TODO: Import from feature-auth when ready
                // LoginScreen(navActions = navActions)

                // Placeholder for now
                androidx.compose.material3.Text("Login Screen - TODO")
            }

            composable<RegisterDestination> {
                // TODO: Import from feature-auth when ready
                // RegisterScreen(navActions = navActions)

                // Placeholder for now
                androidx.compose.material3.Text("Register Screen - TODO")
            }
        }

        // ==================== MOVIES NAVIGATION GRAPH ====================

        navigation<MoviesGraph>(
            startDestination = HomeDestination
        ) {
            composable<HomeDestination> {
                // Import actual HomeRoute from feature-movies
                com.mustafakocer.feature_movies.home.presentation.screen.HomeRoute(
                    navActions = navActions
                )
            }

            composable<SearchDestination> {
                // Import actual SearchRoute from feature-movies
                com.mustafakocer.feature_movies.search.presentation.screen.SearchRoute(
                    navActions = navActions
                )
            }

            composable<SettingsDestination> {
                // TODO: SettingsScreen(navActions = navActions)
                androidx.compose.material3.Text("Settings Screen - TODO")
            }

            composable<MovieDetailsDestination> { backStackEntry ->
                val destination = backStackEntry.toRoute<MovieDetailsDestination>()
                MovieDetailsRoute(
                    navController = navController
                )
            }

            composable<MoreMoviesDestination> { backStackEntry ->
                val destination = backStackEntry.toRoute<MoreMoviesDestination>()

                // MovieListRoute'u kullan
                MovieListRoute(
                    categoryEndpoint = destination.category,
                    categoryTitle = destination.categoryTitle,
                    navController = navController
                )
            }
        }
    }
}

