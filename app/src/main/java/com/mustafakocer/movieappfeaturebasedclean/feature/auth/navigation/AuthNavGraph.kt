package com.mustafakocer.movieappfeaturebasedclean.feature.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.account.presentation.screen.AccountRoute
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.welcome.presentation.screen.WelcomeRoute
import com.mustafakocer.movieappfeaturebasedclean.navigation.AccountScreen
import com.mustafakocer.movieappfeaturebasedclean.navigation.AuthFeatureGraph
import com.mustafakocer.movieappfeaturebasedclean.navigation.MoviesFeatureGraph
import com.mustafakocer.movieappfeaturebasedclean.navigation.WelcomeScreen

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
            WelcomeRoute(
                onNavigateToHome = {
                    navController.navigate(MoviesFeatureGraph) {
                        popUpTo(AuthFeatureGraph) { inclusive = true }
                    }
                }
            )
        }

        composable<AccountScreen> {
            AccountRoute(
                onNavigateToWelcome = {
                    navController.navigate(WelcomeScreen) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}