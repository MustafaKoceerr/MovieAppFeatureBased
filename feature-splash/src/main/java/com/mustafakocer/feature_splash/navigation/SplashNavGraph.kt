package com.mustafakocer.feature_splash.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mustafakocer.navigation_contracts.actions.FeatureSplashNavActions
import com.mustafakocer.navigation_contracts.navigation.AuthFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.MoviesFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.SplashFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.SplashScreen
import  com.mustafakocer.feature_splash.presentation.screen.SplashRoute


fun NavGraphBuilder.splashNavGraph(navController: NavController) {
    navigation<SplashFeatureGraph>(
        startDestination = SplashScreen
    ) {
        composable<SplashScreen> {
            val navActions = object : FeatureSplashNavActions {
                override fun navigateToHome() {
                    navController.navigate(MoviesFeatureGraph) {
                        popUpTo(SplashFeatureGraph) { inclusive = true }
                    }
                }

                override fun navigateToWelcome() {
                    navController.navigate(AuthFeatureGraph) {
                        popUpTo(SplashFeatureGraph) { inclusive = true }
                    }
                }

                override fun navigateUp() {
                    navController.navigateUp()
                }
            }

            SplashRoute(navActions = navActions)
        }
    }
}