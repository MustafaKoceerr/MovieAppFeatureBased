package com.mustafakocer.feature_splash.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mustafakocer.navigation_contracts.navigation.SplashFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.SplashScreen
import  com.mustafakocer.feature_splash.presentation.screen.SplashRoute
import com.mustafakocer.navigation_contracts.actions.splash.SplashNavActions
import com.mustafakocer.navigation_contracts.navigation.AuthFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.MoviesFeatureGraph


fun NavGraphBuilder.splashNavGraph(navController: NavController) {
    navigation<SplashFeatureGraph>(
        startDestination = SplashScreen
    ) {
        composable<SplashScreen> {
            val navActions = object : SplashNavActions {
                override fun navigateToHome() {
                    navController.navigate(MoviesFeatureGraph) {
                        // Splash ekranını stack'ten tamamen kaldır.
                        popUpTo(SplashFeatureGraph) { inclusive = true }
                    }
                }

                override fun navigateToWelcome() {
                    navController.navigate(AuthFeatureGraph) {
                        // Splash ekranını stackten tamamen kaldır.
                        popUpTo(SplashFeatureGraph) { inclusive = true }
                    }
                }

            }

            SplashRoute(navActions = navActions)
        }
    }
}