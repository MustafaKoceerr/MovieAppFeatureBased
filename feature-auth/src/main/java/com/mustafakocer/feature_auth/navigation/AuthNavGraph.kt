package com.mustafakocer.feature_auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mustafakocer.feature_auth.presentation.welcome.screen.WelcomeRoute
import com.mustafakocer.navigation_contracts.actions.FeatureAuthNavActions
import com.mustafakocer.navigation_contracts.navigation.AuthFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.MoviesFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.WelcomeScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
) {
    navigation<AuthFeatureGraph>(
        startDestination = WelcomeScreen
    ) {

        composable<WelcomeScreen> {
            val navActions = object : FeatureAuthNavActions {
                override fun navigateToHome() {
                    navController.navigate(MoviesFeatureGraph) {
                        // WelcomeScreen'i stack'ten kaldır.
                        popUpTo(AuthFeatureGraph) {
                            inclusive = true
                        }
                    }
                }

                override fun navigateUp() {
                    navController.navigateUp()
                }

            }


            WelcomeRoute(navActions = navActions)
            // Gelecekte AccountScreen gibi diğer auth ekranları buraya eklenecek.
            // composable<AccountScreen> { ... }
        }

    }


}