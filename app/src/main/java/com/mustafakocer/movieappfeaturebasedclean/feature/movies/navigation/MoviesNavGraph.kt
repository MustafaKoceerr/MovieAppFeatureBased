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
import com.mustafakocer.movieappfeaturebasedclean.navigation.actions.movies.HomeNavActions
import com.mustafakocer.movieappfeaturebasedclean.navigation.actions.movies.MovieDetailsNavActions
import com.mustafakocer.movieappfeaturebasedclean.navigation.actions.movies.MovieListNavActions
import com.mustafakocer.movieappfeaturebasedclean.navigation.actions.movies.SearchNavActions
import com.mustafakocer.movieappfeaturebasedclean.navigation.actions.movies.SettingsNavActions
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
            // Architectural Decision: A `NavActions` interface is implemented here as an anonymous
            // object. This is a form of dependency inversion. The `HomeRoute` composable depends
            // on the `HomeNavActions` interface, not on the `NavController` directly. This makes
            // `HomeRoute` more testable and decoupled from the navigation framework's implementation
            // details. The anonymous object acts as an adapter, translating interface calls into
            // `navController` actions.
            val navActions = object : HomeNavActions {
                override fun navigateToMovieDetails(movieId: Int) {
                    navController.navigate(MovieDetailsScreen(movieId))
                }

                override fun navigateToMovieList(categoryEndpoint: String) {
                    navController.navigate(MovieListScreen(categoryEndpoint))
                }

                override fun navigateToSearch() {
                    navController.navigate(SearchScreen)
                }

                override fun navigateToSettings() {
                    navController.navigate(SettingsScreen)
                }

                override fun navigateToAccount() {
                    navController.navigate(AccountScreen)
                }
            }
            HomeRoute(navActions = navActions)
        }

        composable<MovieListScreen> {
            val navActions = object : MovieListNavActions {
                override fun navigateToMovieDetails(movieId: Int) {
                    navController.navigate(MovieDetailsScreen(movieId))
                }

                override fun navigateUp() {
                    navController.navigateUp()
                }
            }
            MovieListRoute(navActions = navActions)
        }

        composable<MovieDetailsScreen> {
            val navActions = object : MovieDetailsNavActions {
                override fun navigateUp() {
                    navController.navigateUp()
                }
            }
            MovieDetailsRoute(navActions = navActions)
        }

        composable<SearchScreen> {
            val navActions = object : SearchNavActions {
                override fun navigateToMovieDetails(movieId: Int) {
                    navController.navigate(MovieDetailsScreen(movieId))
                }

                override fun navigateUp() {
                    navController.navigateUp()
                }
            }
            SearchRoute(navActions = navActions)
        }

        composable<SettingsScreen> {
            val navActions = object : SettingsNavActions {
                override fun navigateUp() {
                    navController.navigateUp()
                }
            }
            SettingsRoute(
                navActions = navActions,
                onLanguageChanged = onLanguageChanged
            )
        }
    }
}