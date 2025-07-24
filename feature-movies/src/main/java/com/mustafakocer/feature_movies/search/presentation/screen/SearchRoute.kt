package com.mustafakocer.feature_movies.search.presentation.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEffect
import com.mustafakocer.feature_movies.search.presentation.viewmodel.SearchViewModel
import com.mustafakocer.navigation_contracts.actions.movies.SearchNavActions
import kotlinx.coroutines.flow.collectLatest

/**
 * A "Route" composable that acts as the smart container for the [SearchScreen].
 *
 * Its primary responsibilities are:
 * 1.  Connecting the UI to the [SearchViewModel].
 * 2.  Collecting and observing the UI state in a lifecycle-aware manner.
 * 3.  Handling one-time side effects, including navigation and UI manipulations like hiding the keyboard.
 * 4.  Passing the state and event callbacks down to the stateless [SearchScreen].
 *
 * @param navActions Provides navigation callbacks for moving to other screens or navigating up.
 * @param viewModel The Hilt-injected [SearchViewModel] instance for this screen.
 */
@Composable
fun SearchRoute(
    navActions: SearchNavActions,
    viewModel: SearchViewModel = hiltViewModel(),
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is SearchEffect.NavigateToMovieDetail -> {
                    navActions.navigateToMovieDetails(effect.movieId)
                }
                is SearchEffect.NavigateBack -> {
                    navActions.navigateUp()
                }
                is SearchEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(message = effect.message)
                }
                is SearchEffect.HideKeyboard -> {
                    keyboardController?.hide()
                    // UX Rationale: Clearing focus after hiding the keyboard is good practice.
                    // It removes the blinking cursor from the search field and prevents the
                    // keyboard from reappearing if the user accidentally taps the screen.
                    focusManager.clearFocus()
                }
            }
        }
    }

    SearchScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}