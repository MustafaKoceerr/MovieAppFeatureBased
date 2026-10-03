package com.mustafakocer.movieappfeaturebasedclean.feature.splash.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mustafakocer.movieappfeaturebasedclean.feature.splash.presentation.screen.SplashRoute
import com.mustafakocer.movieappfeaturebasedclean.navigation.AuthFeatureGraph
import com.mustafakocer.movieappfeaturebasedclean.navigation.MoviesFeatureGraph
import com.mustafakocer.movieappfeaturebasedclean.navigation.SplashFeatureGraph
import com.mustafakocer.movieappfeaturebasedclean.navigation.SplashScreen

fun NavGraphBuilder.splashNavGraph(navController: NavController) {
    navigation<SplashFeatureGraph>(
        startDestination = SplashScreen
    ) {
        composable<SplashScreen> {
            SplashRoute(
                onNavigateToHome = {
                    navController.navigate(MoviesFeatureGraph) {
                        popUpTo(SplashFeatureGraph) { inclusive = true }
                    }
                },
                onNavigateToWelcome = {
                    navController.navigate(AuthFeatureGraph) {
                        popUpTo(SplashFeatureGraph) { inclusive = true }
                    }
                },
            )
        }
    }
}
