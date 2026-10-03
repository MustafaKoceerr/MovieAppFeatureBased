package com.mustafakocer.movieappfeaturebasedclean.feature.movies.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.screen.MovieDetailsRoute
import com.mustafakocer.movieappfeaturebasedclean.feature.home.presentation.screen.HomeRoute
import com.mustafakocer.movieappfeaturebasedclean.feature.list.presentation.screen.MovieListRoute
import com.mustafakocer.movieappfeaturebasedclean.feature.search.presentation.screen.SearchRoute
import com.mustafakocer.movieappfeaturebasedclean.feature.settings.presentation.screen.SettingsRoute
import com.mustafakocer.movieappfeaturebasedclean.navigation.AccountScreen
import com.mustafakocer.movieappfeaturebasedclean.navigation.HomeScreen
import com.mustafakocer.movieappfeaturebasedclean.navigation.MovieDetailsScreen
import com.mustafakocer.movieappfeaturebasedclean.navigation.MovieListScreen
import com.mustafakocer.movieappfeaturebasedclean.navigation.MoviesFeatureGraph
import com.mustafakocer.movieappfeaturebasedclean.navigation.SearchScreen
import com.mustafakocer.movieappfeaturebasedclean.navigation.SettingsScreen

/**
 * Defines the encapsulated navigation graph for the entire movies feature.
 *
 * Architectural Decision: Using a nested navigation graph (`navigation(...)`) for the `movies`
 * feature promotes modularity and encapsulation. It groups all related screens under a single
 * logical unit (`MoviesFeatureGraph`). This makes the overall navigation structure of the app
 * cleaner, easier to manage, and allows for better separation of concerns between different
 * feature modules.
 *
 * @param navController The top-level NavController used for navigating between screens.
 * @param onLanguageChanged A hoisted callback function to be invoked when an action within this
 *                          graph requires a full application restart (e.g., after a language change).
 *                          This is a key architectural pattern that keeps feature modules decoupled
 *                          from the `Activity` context, as only the `Activity` can perform a restart.
 */
fun NavGraphBuilder.moviesNavGraph(
    navController: NavController,
    onLanguageChanged: () -> Unit,
) {

    navigation<MoviesFeatureGraph>(
        startDestination = HomeScreen,
    ) {
        // Defines the composable for the Home screen destination.
        composable<HomeScreen> {
            HomeRoute(
                onNavigateToMovieDetails = { navController.navigate(MovieDetailsScreen(it)) },
                onNavigateToMovieList = { navController.navigate(MovieListScreen(it)) },
                onNavigateToSearch = { navController.navigate(SearchScreen) },
                onNavigateToSettings = { navController.navigate(SettingsScreen) },
                onNavigateToAccount = { navController.navigate(AccountScreen) },
            )
        }

        composable<MovieListScreen> {
            MovieListRoute(
                onNavigateToMovieDetails = { navController.navigate(MovieDetailsScreen(it)) },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable<MovieDetailsScreen> {
            MovieDetailsRoute(onNavigateUp = { navController.navigateUp() })
        }

        composable<SearchScreen> {
            SearchRoute(
                onNavigateToMovieDetails = { navController.navigate(MovieDetailsScreen(it)) },
                onNavigateUp = { navController.navigateUp() },
            )
        }

        composable<SettingsScreen> {
            SettingsRoute(
                onNavigateUp = { navController.navigateUp() },
                onLanguageChanged = onLanguageChanged
            )
        }
    }
}