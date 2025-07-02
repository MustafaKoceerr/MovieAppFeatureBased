package com.mustafakocer.movieappfeaturebasedclean.navigation


import androidx.navigation.NavHostController
import com.mustafakocer.navigation_contracts.*
import com.mustafakocer.navigation_contracts.destinations.*

/**
 * Implementation of navigation contracts in app module
 *
 * ARCHITECTURE:
 * ✅ Implements contracts from navigation-contracts module
 * ✅ Uses type-safe destinations
 * ✅ Handles backstack management
 * ✅ No circular dependencies
 */
class AppNavigationActionsImpl(
    private val navController: NavHostController,
) : MoviesNavActions, AuthNavActions, SearchNavActions, SettingsNavActions {

    // ==================== BASE NAVIGATION ====================

    override fun navigateBack() {
        navController.navigateUp()
    }

    // ==================== MOVIES NAVIGATION ====================

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

    override fun navigateToHome() {
        navController.navigate(HomeDestination) {
            // Clear backstack to home
            popUpTo(MoviesGraph) {
                inclusive = false
                saveState = false
            }
            launchSingleTop = true
        }
    }

    override fun navigateToAuth() {
        navController.navigate(AuthGraph) {
            // Clear movies graph from backstack when logging out
            popUpTo(MoviesGraph) {
                inclusive = true
                saveState = false
            }
            launchSingleTop = true
        }
    }

    // ==================== AUTH NAVIGATION ====================

    override fun navigateToRegister() {
        navController.navigate(RegisterDestination)
    }

    override fun navigateToMainApp() {
        navController.navigate(MoviesGraph) {
            // Clear auth graph from backstack after successful login
            popUpTo(AuthGraph) {
                inclusive = true
                saveState = false
            }
            launchSingleTop = true
        }
    }

    // ==================== SEARCH NAVIGATION ====================
    // SearchNavActions extends MoviesNavActions, so it inherits:
    // - navigateToMovieDetails()
    // - navigateToHome()
    // - navigateBack()

    // ==================== SETTINGS NAVIGATION ====================
    // SettingsNavActions extends BaseNavActions, so it inherits:
    // - navigateToAuth()
    // - navigateBack()
}