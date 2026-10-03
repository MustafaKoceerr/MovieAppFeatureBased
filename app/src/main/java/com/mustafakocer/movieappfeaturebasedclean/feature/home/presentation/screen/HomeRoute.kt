package com.mustafakocer.movieappfeaturebasedclean.feature.home.presentation.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.movieappfeaturebasedclean.feature.home.presentation.viewmodel.HomeViewModel

@Composable
fun HomeRoute(
    onNavigateToMovieDetails: (movieId: Int) -> Unit,
    onNavigateToMovieList: (categoryEndpoint: String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAccount: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    HomeScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onRefresh = viewModel::onRefresh,
        onMovieClick = onNavigateToMovieDetails,
        onViewAllClick = { category -> onNavigateToMovieList(category.apiEndpoint) },
        onSearchClick = onNavigateToSearch,
        onSettingsClick = onNavigateToSettings,
        onAccountClick = onNavigateToAccount,
    )
}
