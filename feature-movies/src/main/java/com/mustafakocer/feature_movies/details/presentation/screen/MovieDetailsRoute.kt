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
import com.mustafakocer.navigation_contracts.actions.FeatureMoviesNavActions
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MovieDetailsRoute(
    navActions: FeatureMoviesNavActions,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarErrorMessage: String = stringResource(R.string.error_share_movie)
    val chooserTitle = stringResource(R.string.share_text_title)

    // Handle UI Effects (side effects)
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
                        // ✅ ERROR: Show error snackbar if sharing fails
                        snackbarHostState.showSnackbar(
                            message = snackbarErrorMessage,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is MovieDetailsEffect.ShowSnackbar -> {
                    // ✅ SIMPLIFIED: All snackbars are short/long, no persistence
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