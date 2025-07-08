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
                // TODO: SearchScreen(navActions = navActions)
                androidx.compose.material3.Text("Search Screen - TODO")
            }

            composable<SettingsDestination> {
                // TODO: SettingsScreen(navActions = navActions)
                androidx.compose.material3.Text("Settings Screen - TODO")
            }

            composable<MovieDetailsDestination> { backStackEntry ->
                val destination = backStackEntry.toRoute<MovieDetailsDestination>()
                MovieDetailsRoute(
                    movieId = destination.movieId, // ✅ movieId'yi destination'dan al
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

/**
 * Implementation of all navigation actions
 */
private class AppNavActionsImpl(
    private val navController: NavHostController,
) : MoviesNavActions, AuthNavActions, CommonNavActions {

    // ==================== MOVIES ACTIONS ====================

    override fun navigateToMovieDetails(movieId: Int) {
        val destination = DestinationFactory.movieDetails(movieId)
        navController.navigate(destination)
    }

    override fun navigateToMoreMovies(category: String, categoryTitle: String) {
        val destination = DestinationFactory.moreMovies(category, categoryTitle)
        navController.navigate(destination)
    }

    override fun navigateToSearch() {
        navController.navigate(SearchDestination)
    }

    override fun navigateToSettings() {
        navController.navigate(SettingsDestination)
    }

    override fun navigateToAuth() {
        navController.navigate(AuthGraph) {
            // Clear movies graph from backstack
            popUpTo(MoviesGraph) { inclusive = true }
        }
    }

    // ==================== AUTH ACTIONS ====================

    override fun navigateToRegister() {
        navController.navigate(RegisterDestination)
    }

    override fun navigateToMainApp() {
        navController.navigate(MoviesGraph) {
            // Clear auth graph from backstack
            popUpTo(AuthGraph) { inclusive = true }
        }
    }

    // ==================== COMMON ACTIONS ====================

    override fun navigateBack() {
        navController.navigateUp()
    }

    override fun navigateToHome() {
        navController.navigate(HomeDestination) {
            // Clear everything and start fresh
            popUpTo(MoviesGraph) { inclusive = false }
        }
    }
}