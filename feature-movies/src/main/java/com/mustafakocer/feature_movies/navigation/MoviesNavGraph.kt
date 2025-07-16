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
import com.mustafakocer.navigation_contracts.actions.FeatureMoviesNavActions
import com.mustafakocer.navigation_contracts.navigation.*

fun NavGraphBuilder.moviesNavGraph(
    navController: NavController,
    onLanguageChanged: () -> Unit,
) {

    val navActions = object : FeatureMoviesNavActions {
        override fun navigateToMovieDetails(movieId: Int) {
            navController.navigate(MovieDetailsScreen(movieId = movieId))
        }

        override fun navigateToMovieList(categoryTitle: String, categoryEndpoint: String) {
            navController.navigate(
                MovieListScreen(
                    categoryTitle = categoryTitle,
                    categoryEndpoint = categoryEndpoint
                )
            )
        }

        override fun navigateToSearch() {
            navController.navigate(SearchScreen)
        }

        override fun navigateToSettings() {
            navController.navigate(SettingsScreen)
        }

        override fun navigateUp() {
            navController.navigateUp()
        }
    }

    navigation<MoviesFeatureGraph>(
        startDestination = HomeScreen
    ) {
        composable<HomeScreen> { HomeRoute(navActions = navActions) }
        composable<MovieListScreen> { MovieListRoute(navActions = navActions) }
        composable<MovieDetailsScreen> { MovieDetailsRoute(navActions = navActions) }
        composable<SearchScreen> { SearchRoute(navActions = navActions) }
        composable<SettingsScreen> {
            SettingsRoute(
                navActions = navActions,
                onLanguageChanged = onLanguageChanged
            )
        }
    }
}