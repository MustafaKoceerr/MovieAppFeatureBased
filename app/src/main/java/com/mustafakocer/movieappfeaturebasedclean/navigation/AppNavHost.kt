package com.mustafakocer.movieappfeaturebasedclean.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.mustafakocer.feature_movies.navigation.moviesNavGraph
import androidx.activity.compose.LocalActivity
import com.mustafakocer.feature_auth.navigation.authNavGraph
import com.mustafakocer.feature_splash.navigation.splashNavGraph
import com.mustafakocer.navigation_contracts.navigation.SplashFeatureGraph

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: SplashFeatureGraph = SplashFeatureGraph,
) {
    val activity = LocalActivity.current
    NavHost(
        navController = navController,
        startDestination = startDestination, // Örn: MoviesFeatureGraph
        modifier = modifier
    ) {
        // :app modülü artık HomeRoute'u, SearchRoute'u vs. BİLMİYOR.
        // Sadece :feature-movies modülünün dışarıya açtığı
        // tek bir fonksiyonu çağırıyor.

        // YENİ BAŞLANGIÇ NOKTASI
        splashNavGraph(navController = navController)

        moviesNavGraph(
            navController = navController,
            // :feature-movies modülünün istediği "eylemi" burada tanımlıyoruz.
            onLanguageChanged = { activity?.recreate() }
        )

        authNavGraph(
            navController = navController
        )

        // Gelecekte bir :feature-profile eklendiğinde,
        // Auth grafiği gibi app-level grafikler burada tanımlanabilir.
        // authNavGraph(navController)
    }
}
