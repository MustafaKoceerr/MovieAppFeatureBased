package com.mustafakocer.feature_movies.list.presentation.screen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEffect
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEvent
import com.mustafakocer.feature_movies.list.presentation.viewmodel.MovieListViewModel
import com.mustafakocer.navigation_contracts.MovieListNavActions

/**
 * Movie List Route
 *
 * RESPONSIBILITY: Handle navigation effects and side effects
 * PATTERN: Route handles effects, Screen handles pure UI
 * */
@Composable
fun MovieListRoute(
    categoryEndpoint: String,
    categoryTitle: String,
    navActions: MovieListNavActions,
    viewModel: MovieListViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle UI Effects
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                // ==================== NAVIGATION EFFECTS ====================
                is MovieListEffect.NavigateToMovieDetail -> {
                    navActions.navigateToMovieDetails(
                        movieId = effect.route.movieId
                    )
                }

                is MovieListEffect.NavigateBack -> {
                    navActions.navigateBack()
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
    MovieListScreen(
        state = state,
        onEvent = viewModel::onEvent // Event'leri doğrudan paslamak daha temiz
    )
}