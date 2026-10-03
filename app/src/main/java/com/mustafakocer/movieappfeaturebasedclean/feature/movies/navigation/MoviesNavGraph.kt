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
 * A nested navigation graph (`navigation(...)`) groups all movie screens under
 * [MoviesFeatureGraph]. Routes receive plain navigation lambdas, not the NavController.
 *
 * @param navController The top-level NavController used for navigating between screens.
 * @param onLanguageChanged A hoisted callback function to be invoked when an action within this
 *                          graph requires a full application restart (e.g., after a language change).
 *                          Only the `Activity` can perform a restart.
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