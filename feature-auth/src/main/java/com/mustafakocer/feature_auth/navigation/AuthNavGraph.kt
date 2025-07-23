package com.mustafakocer.feature_auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mustafakocer.feature_auth.account.presentation.screen.AccountRoute
import com.mustafakocer.feature_auth.welcome.presentation.screen.WelcomeRoute
import com.mustafakocer.navigation_contracts.actions.auth.AccountNavActions
import com.mustafakocer.navigation_contracts.actions.auth.WelcomeNavActions
import com.mustafakocer.navigation_contracts.navigation.AccountScreen
import com.mustafakocer.navigation_contracts.navigation.AuthFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.MoviesFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.WelcomeScreen

/**
 * Defines the nested navigation graph for the authentication feature.
 *
 * @param navController The top-level NavController for the application.
 *
 * Architectural Note:
 * This function encapsulates the entire navigation flow for the authentication feature. Its most
 * critical role is to provide the concrete implementations for the `WelcomeNavActions` and
 * `AccountNavActions` interfaces. This is where the abstract navigation contracts are fulfilled,
 * connecting the feature's requests (e.g., `navigateToHome`) to the actual `navController`
 * actions. This pattern is essential for decoupling the feature module from the rest of the app.
 */
fun NavGraphBuilder.authNavGraph(
    navController: NavController,
) {
    navigation<AuthFeatureGraph>(
        startDestination = WelcomeScreen
    ) {
        composable<WelcomeScreen> {
            val navActions = object : WelcomeNavActions {
                override fun navigateToHome() {
                    navController.navigate(MoviesFeatureGraph) {
                        // Clear the auth graph from the back stack upon successful login.
                        popUpTo(AuthFeatureGraph) { inclusive = true }
                    }
                }
            }
            WelcomeRoute(navActions = navActions)
        }

        composable<AccountScreen> {
            val navActions = object : AccountNavActions {
                override fun navigateToWelcome() {
                    navController.navigate(WelcomeScreen) {
                        // On logout, clear the entire back stack and start fresh at the Welcome screen.
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                override fun navigateUp() {
                    navController.navigateUp()
                }
            }
            AccountRoute(navActions = navActions)
        }
    }
}