package com.mustafakocer.feature_movies.home.presentation.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_movies.home.presentation.contract.HomeEffect
import com.mustafakocer.feature_movies.home.presentation.viewmodel.HomeViewModel
import com.mustafakocer.navigation_contracts.actions.movies.HomeNavActions
import kotlinx.coroutines.flow.collectLatest

/**
 * The main entry point and controller for the Home screen feature.
 *
 * @param navActions An implementation of [HomeNavActions] for triggering navigation.
 * @param viewModel The ViewModel responsible for the screen's business logic.
 *
 * Architectural Note:
 * This Composable acts as the "route" or controller, orchestrating interactions between the
 * ViewModel and the UI/navigation layers. Its key responsibilities are:
 * 1.  **State Observation:** It collects `uiState` from the ViewModel in a lifecycle-aware manner.
 * 2.  **Effect Handling:** It uses a `LaunchedEffect` to handle one-time side effects like
 *     navigation (via the `HomeNavActions` contract) and showing Snackbars.
 * 3.  **UI Delegation:** It passes the collected state and event handlers down to the
 *     stateless `HomeScreen` Composable.
 */
@Composable
fun HomeRoute(
    navActions: HomeNavActions,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // This LaunchedEffect handles all one-time side effects from the ViewModel.
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is HomeEffect.NavigateToMovieDetails -> navActions.navigateToMovieDetails(effect.movieId)
                is HomeEffect.NavigateToMovieList -> navActions.navigateToMovieList(effect.categoryEndpoint)
                is HomeEffect.NavigateToSettings -> navActions.navigateToSettings()
                is HomeEffect.NavigateToSearch -> navActions.navigateToSearch()
                is HomeEffect.NavigateToAccount -> navActions.navigateToAccount()
                is HomeEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
            }
        }
    }

    // Pass the state and event handler to the pure UI component.
    HomeScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}