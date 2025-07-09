package com.mustafakocer.movieappfeaturebasedclean.navigation

import androidx.navigation.NavHostController
import com.mustafakocer.navigation_contracts.*
import com.mustafakocer.navigation_contracts.destinations.*
/**
 * Implementation of all navigation actions
 */
class AppNavActionsImpl(
    private val navController: NavHostController,
) : MoviesNavActions, AuthNavActions, CommonNavActions, SearchNavActions {

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