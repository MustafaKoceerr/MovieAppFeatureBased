package com.mustafakocer.feature_movies.search.presentation.screen


import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_movies.search.presentation.contract.SearchEffect
import com.mustafakocer.feature_movies.search.presentation.viewmodel.SearchViewModel
import com.mustafakocer.navigation_contracts.actions.FeatureMoviesNavActions
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.getValue


@Composable
fun SearchRoute(
    navActions: FeatureMoviesNavActions,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Klavye ve focus yönetimi için gerekli controller'ları alıyoruz.
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Yan etkileri dinleyip yöneten bölüm.
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
                    focusManager.clearFocus() // Klavyeyi gizledikten sonra focus'u da temizlemek iyidir.
                }
            }
        }
    }

    // Saf UI bileşenini çağırıyoruz.
    SearchScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}