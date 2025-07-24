package com.mustafakocer.feature_movies.details.presentation.screen

import android.content.Intent
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEffect
import com.mustafakocer.feature_movies.details.presentation.viewmodel.MovieDetailsViewModel
import com.mustafakocer.navigation_contracts.actions.movies.MovieDetailsNavActions
import kotlinx.coroutines.flow.collectLatest

/**
 * The main entry point and controller for the Movie Details screen feature.
 *
 * @param navActions An implementation of [MovieDetailsNavActions] for triggering navigation.
 * @param viewModel The ViewModel responsible for the screen's business logic.
 *
 * Architectural Note:
 * This Composable acts as the "route" or controller, orchestrating interactions between the
 * ViewModel and the UI/platform layers. Its key responsibilities are:
 * 1.  **State Observation:** It collects `uiState` from the ViewModel in a lifecycle-aware manner.
 * 2.  **Effect Handling:** It uses a `LaunchedEffect` to handle one-time side effects like
 *     navigation (via the `navActions` contract) and showing Snackbars.
 * 3.  **Platform Interaction:** It contains platform-specific logic, such as creating and
 *     launching the `ACTION_SEND` Intent, keeping the ViewModel free of Android `Context`.
 * 4.  **UI Delegation:** It passes the collected state and event handlers down to the
 *     stateless `MovieDetailsScreen` Composable.
 */
@Composable
fun MovieDetailsRoute(
    navActions: MovieDetailsNavActions,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarErrorMessage: String = stringResource(R.string.error_share_movie)
    val chooserTitle = stringResource(R.string.share_text_title)

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is MovieDetailsEffect.NavigateBack -> {
                    navActions.navigateUp()
                }
                is MovieDetailsEffect.ShareContent -> {
                    try {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, effect.content)
                        }
                        val chooserIntent = Intent.createChooser(shareIntent, chooserTitle)
                        context.startActivity(chooserIntent)
                    } catch (e: Exception) {
                        // Fallback if no app can handle the share intent.
                        snackbarHostState.showSnackbar(
                            message = snackbarErrorMessage,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                is MovieDetailsEffect.ShowSnackbar -> {
                    val duration =
                        if (effect.isError) SnackbarDuration.Long else SnackbarDuration.Short
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = duration
                    )
                }
            }
        }
    }

    MovieDetailsScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}