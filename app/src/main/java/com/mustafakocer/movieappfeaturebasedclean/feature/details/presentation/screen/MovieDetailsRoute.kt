package com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.screen

import android.content.Intent
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.movieappfeaturebasedclean.R
import com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.viewmodel.MovieDetailsViewModel
import kotlinx.coroutines.launch

@Composable
fun MovieDetailsRoute(
    onNavigateUp: () -> Unit,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val shareErrorMessage = stringResource(R.string.error_share_movie)
    val chooserTitle = stringResource(R.string.share_text_title)

    // Sharing is a pure UI action (launching a chooser), so it needs no ViewModel round trip.
    val onShareClick: (String) -> Unit = { content ->
        try {
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, content)
            }
            context.startActivity(Intent.createChooser(sendIntent, chooserTitle))
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = shareErrorMessage,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    MovieDetailsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = onNavigateUp,
        onRefresh = viewModel::onRefresh,
        onShareClick = onShareClick,
    )
}
