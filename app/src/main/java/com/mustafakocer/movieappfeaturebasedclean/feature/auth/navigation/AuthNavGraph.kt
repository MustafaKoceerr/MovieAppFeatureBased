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
 * The routes receive plain navigation lambdas, so screens never depend on the NavController.
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