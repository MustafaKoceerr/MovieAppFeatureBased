package com.mustafakocer.feature_auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mustafakocer.feature_auth.account.presentation.screen.AccountRoute
import com.mustafakocer.feature_auth.welcome.presentation.screen.WelcomeRoute
import com.mustafakocer.feature_auth.welcome.presentation.screen.WelcomeScreen
import com.mustafakocer.navigation_contracts.actions.auth.AccountNavActions
import com.mustafakocer.navigation_contracts.actions.auth.WelcomeNavActions
import com.mustafakocer.navigation_contracts.navigation.AccountScreen
import com.mustafakocer.navigation_contracts.navigation.AuthFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.MoviesFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.WelcomeScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
) {
    navigation<AuthFeatureGraph>(
        startDestination = WelcomeScreen
    ) {
        // WelcomeScreen için özel NavActions
        composable<WelcomeScreen> {
            val navActions = object : WelcomeNavActions {
                override fun navigateToHome() {
                    navController.navigate(MoviesFeatureGraph) {
                        popUpTo(AuthFeatureGraph) { inclusive = true }
                    }
                }
            }
            WelcomeRoute(navActions = navActions)
        }

        // AccountScreen için özel NavActions
        composable<AccountScreen> {
            val navActions = object : AccountNavActions {
                override fun navigateToWelcome() {
                    navController.navigate(WelcomeScreen) {
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


