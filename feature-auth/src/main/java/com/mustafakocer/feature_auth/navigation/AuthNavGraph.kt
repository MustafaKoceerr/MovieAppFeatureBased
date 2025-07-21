package com.mustafakocer.feature_auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mustafakocer.feature_auth.account.presentation.screen.AccountRoute
import com.mustafakocer.feature_auth.welcome.presentation.screen.WelcomeRoute
import com.mustafakocer.navigation_contracts.actions.FeatureAuthNavActions
import com.mustafakocer.navigation_contracts.navigation.AccountScreen
import com.mustafakocer.navigation_contracts.navigation.AuthFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.MoviesFeatureGraph
import com.mustafakocer.navigation_contracts.navigation.WelcomeScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
) {
    val navActions = object : FeatureAuthNavActions {
        override fun navigateToHome() {
            navController.navigate(MoviesFeatureGraph) {
                // Auth grafiğini yığından tamamen kaldır.
                popUpTo(AuthFeatureGraph) {
                    inclusive = true
                }
            }
        }

        override fun navigateToWelcome() {
            // Çıkış yapıldığında, en başa (WelcomeScreen'e) dön
            // ve aradaki tüm ekranların geçmişini temizle.
            navController.navigate(WelcomeScreen) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                // Yeni hedefi yığında tek bir kopya olarak tut.
                launchSingleTop = true
            }
        }

        override fun navigateUp() {
            navController.navigateUp()
        }
    }

    navigation<AuthFeatureGraph>(
        startDestination = WelcomeScreen,
    ) {
        composable<WelcomeScreen> { WelcomeRoute(navActions = navActions) }
        composable<AccountScreen> { AccountRoute(navActions = navActions) }
    }
}