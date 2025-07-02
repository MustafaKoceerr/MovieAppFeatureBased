package com.mustafakocer.movieappfeaturebasedclean

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mustafakocer.core_ui.ui.theme.MovieDiscoveryTheme
import com.mustafakocer.movieappfeaturebasedclean.navigation.AppNavHost
import com.mustafakocer.navigation_contracts.destinations.MoviesGraph
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity with integrated navigation
 *
 * RESPONSIBILITIES:
 * ✅ Setup navigation controller
 * ✅ Determine start destination (auth vs main app)
 * ✅ Apply app theme
 * ✅ Handle system UI (edge-to-edge)
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MovieDiscoveryTheme {
                val navController = rememberNavController()

                // TODO: Determine start destination based on auth state
                // For now, start with Movies (since auth is not implemented yet)
                val startDestination = determineStartDestination()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    /**
     * Determine which graph to start with based on user state
     *
     * TODO: Implement proper auth state checking
     * - Check if user is logged in (SharedPreferences/DataStore)
     * - Check if user completed onboarding
     * - Return appropriate start destination
     */
    private fun determineStartDestination(): Any {
        // For now, always start with Movies since auth is not implemented
        // Later: Check user auth state and return AuthGraph or MoviesGraph

        return MoviesGraph

        // Future implementation:
        // return if (isUserLoggedIn()) MoviesGraph else AuthGraph
    }

    // TODO: Implement when auth feature is ready
    // private fun isUserLoggedIn(): Boolean {
    //     // Check auth state from repository/datastore
    //     return false
    // }
}
