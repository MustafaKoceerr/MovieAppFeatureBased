package com.mustafakocer.feature_movies.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mustafakocer.feature_movies.details.presentation.screen.MovieDetailsRoute
import com.mustafakocer.feature_movies.home.presentation.screen.HomeRoute
import com.mustafakocer.feature_movies.list.presentation.screen.MovieListRoute
import com.mustafakocer.feature_movies.search.presentation.screen.SearchRoute
import com.mustafakocer.feature_movies.settings.presentation.screen.SettingsRoute
import com.mustafakocer.navigation_contracts.actions.movies.* // <-- YENİ IMPORT
import com.mustafakocer.navigation_contracts.navigation.*

fun NavGraphBuilder.moviesNavGraph(
    navController: NavController,
    onLanguageChanged: () -> Unit,
) {
    navigation<MoviesFeatureGraph>(
        startDestination = HomeScreen,
    ) {
        composable<HomeScreen> {
            val navActions = object : HomeNavActions {
                override fun navigateToMovieDetails(movieId: Int) { navController.navigate(MovieDetailsScreen(movieId)) }
                override fun navigateToMovieList(categoryEndpoint: String) { navController.navigate(MovieListScreen(categoryEndpoint)) }
                override fun navigateToSearch() { navController.navigate(SearchScreen) }
                override fun navigateToSettings() { navController.navigate(SettingsScreen) }
                override fun navigateToAccount() { navController.navigate(AccountScreen) }
            }
            HomeRoute(navActions = navActions)
        }

        composable<MovieListScreen> {
            val navActions = object : MovieListNavActions {
                override fun navigateToMovieDetails(movieId: Int) { navController.navigate(MovieDetailsScreen(movieId)) }
                override fun navigateUp() { navController.navigateUp() }
            }
            MovieListRoute(navActions = navActions)
        }

        composable<MovieDetailsScreen> {
            val navActions = object : MovieDetailsNavActions {
                override fun navigateUp() { navController.navigateUp() }
            }
            MovieDetailsRoute(navActions = navActions)
        }

        composable<SearchScreen> {
            val navActions = object : SearchNavActions {
                override fun navigateToMovieDetails(movieId: Int) { navController.navigate(MovieDetailsScreen(movieId)) }
                override fun navigateUp() { navController.navigateUp() }
            }
            SearchRoute(navActions = navActions)
        }

        composable<SettingsScreen> {
            val navActions = object : SettingsNavActions {
                override fun navigateUp() { navController.navigateUp() }
            }
            SettingsRoute(
                navActions = navActions,
                onLanguageChanged = onLanguageChanged
            )
        }
    }
}