package com.mustafakocer.feature_movies.search.presentation.screen


import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEffect
import com.mustafakocer.feature_movies.search.presentation.viewmodel.SearchViewModel
import com.mustafakocer.navigation_contracts.SearchNavActions

/**
 * SearchRoute - Handle effects and navigation
 *
 * RESPONSIBILITY: Handle side effects and navigation
 * PATTERN: Route handles effects, Screen handles pure UI
 */
@Composable
fun SearchRoute(
    navActions: SearchNavActions,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Handle UI Effects
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                // ==================== NAVIGATION EFFECTS ====================
                is SearchEffect.NavigateToMovieDetail -> {
                    navActions.navigateToMovieDetails(effect.movieId)
                }

                is SearchEffect.NavigateBack -> {
                    navActions.navigateBack()
                }

                // ==================== UI FEEDBACK EFFECTS ====================
                is SearchEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Short
                    )
                }

                // ==================== KEYBOARD EFFECTS ====================
                is SearchEffect.HideKeyboard -> {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            }
        }
    }

    // Render the screen
    SearchScreen(
        contract = viewModel,
        snackbarHostState = snackbarHostState
    )
}

