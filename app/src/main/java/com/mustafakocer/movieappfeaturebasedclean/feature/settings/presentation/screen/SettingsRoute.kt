package com.mustafakocer.movieappfeaturebasedclean.feature.settings.presentation.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.movieappfeaturebasedclean.feature.settings.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsRoute(
    onNavigateUp: () -> Unit,
    onLanguageChanged: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.restartRequired) {
        if (state.restartRequired) {
            viewModel.onRestartHandled()
            onLanguageChanged()
        }
    }

    SettingsScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = onNavigateUp,
        onThemeSelected = viewModel::onThemeSelected,
        onLanguageSelected = viewModel::onLanguageSelected,
        onErrorShown = viewModel::onErrorShown,
    )
}
