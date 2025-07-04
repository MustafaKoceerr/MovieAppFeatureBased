package com.mustafakocer.feature_movies.list.presentation.screen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mustafakocer.feature_movies.home.presentation.contract.MoviesListRoute
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEffect
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEvent
import com.mustafakocer.feature_movies.list.presentation.viewmodel.MovieListViewModel

/**
 * Movie List Route
 *
 * RESPONSIBILITY: Handle navigation effects and side effects
 * PATTERN: Route handles effects, Screen handles pure UI
 * */
@Composable
fun MoviesListRoute(
    categoryEndpoint: String,
    categoryTitle: String,
    navController: NavHostController,
    viewModel: MovieListViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    // Handle UI Effects
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                // ==================== NAVIGATION EFFECTS ====================
                is MovieListEffect.NavigateToMovieDetail -> {
                    // todo navigate to detail
                }

                is MovieListEffect.NavigateBack -> {
                    navController.popBackStack()
                }

                // ==================== UI FEEDBACK EFFECTS ====================
                is MovieListEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is MovieListEffect.ShowError -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Initialize screen with parameters
    LaunchedEffect(categoryEndpoint, categoryTitle) {
        viewModel.onEvent(
            MovieListEvent.InitializeScreen(
                categoryEndpoint = categoryEndpoint,
                categoryTitle = categoryTitle
            )
        )
    }

    // Render the screen
    MovieListScreen(contract = viewModel)
}