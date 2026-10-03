package com.mustafakocer.feature_movies.list.presentation.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_movies.list.presentation.contract.MovieListEffect
import com.mustafakocer.feature_movies.list.presentation.viewmodel.MovieListViewModel
import com.mustafakocer.navigation_contracts.actions.movies.MovieListNavActions
import kotlinx.coroutines.flow.collectLatest

/**
 * A "Route" composable that acts as the smart container for the [MovieListScreen].
 *
 * Its primary responsibilities are:
 * 1.  Connecting the UI to the [MovieListViewModel].
 * 2.  Collecting and observing the UI state in a lifecycle-aware manner.
 * 3.  Handling one-time side effects (like navigation or showing Snackbars) triggered by the ViewModel.
 * 4.  Passing the state and event callbacks down to the stateless [MovieListScreen].
 *
 * @param navActions Provides navigation callbacks for moving to other screens or navigating up.
 * @param viewModel The Hilt-injected [MovieListViewModel] instance for this screen.
 */
@Composable
fun MovieListRoute(
    navActions: MovieListNavActions,
    viewModel: MovieListViewModel = hiltViewModel(),
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // The state for managing Snackbars is hoisted here to be controlled by side effects from the ViewModel.
    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is MovieListEffect.NavigateToMovieDetail -> {
                    navActions.navigateToMovieDetails(effect.movieId)
                }

                is MovieListEffect.NavigateBack -> {
                    navActions.navigateUp()
                }

                is MovieListEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
            }
        }
    }

    // The stateless ("dumb") UI component is invoked here. It receives the current state
    // and a lambda to forward user events to the ViewModel. This separation of concerns makes
    // the `MovieListScreen` highly reusable and easy to preview in isolation.
    MovieListScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}