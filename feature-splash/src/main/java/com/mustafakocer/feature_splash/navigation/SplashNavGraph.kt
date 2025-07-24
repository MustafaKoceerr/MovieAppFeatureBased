package com.mustafakocer.feature_splash.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mustafakocer.feature_splash.presentation.screen.SplashRoute
import com.mustafakocer.navigation_contracts.actions.splash.SplashNavActions
import com.mustafakocer.navigation_contracts.navigation.AuthFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.MoviesFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.SplashFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.SplashScreen

/**
 * Defines the nested navigation graph for the splash feature.
 *
 * @param navController The top-level NavController for the application.
 *
 * Architectural Note:
 * This extension function encapsulates the entire navigation flow for the splash feature.
 * Its most critical role is to provide the concrete implementation for the `SplashNavActions`
 * interface. This is where the abstract navigation contract is fulfilled. By creating an
 * anonymous object that implements the interface, we connect the feature's requests (e.g.,
 * `navigateToHome`) to the actual `navController` actions that navigate to other feature graphs.
 * The `popUpTo` logic is essential here to ensure the splash screen is completely removed from
 * the back stack after its purpose is served.
 */
fun NavGraphBuilder.splashNavGraph(navController: NavController) {
    navigation<SplashFeatureGraph>(
        startDestination = SplashScreen
    ) {
        composable<SplashScreen> {
            val navActions = object : SplashNavActions {
                override fun navigateToHome() {
                    navController.navigate(MoviesFeatureGraph) {
                        // Remove the entire splash graph from the back stack.
                        popUpTo(SplashFeatureGraph) { inclusive = true }
                    }
                }

                override fun navigateToWelcome() {
                    navController.navigate(AuthFeatureGraph) {
                        // Remove the entire splash graph from the back stack.
                        popUpTo(SplashFeatureGraph) { inclusive = true }
                    }
                }
            }
            SplashRoute(navActions = navActions)
        }
    }
}