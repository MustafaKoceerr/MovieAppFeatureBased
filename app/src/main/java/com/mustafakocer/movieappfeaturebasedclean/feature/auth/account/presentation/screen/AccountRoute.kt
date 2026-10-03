package com.mustafakocer.movieappfeaturebasedclean.feature.auth.account.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.movieappfeaturebasedclean.feature.auth.account.presentation.viewmodel.AccountViewModel

@Composable
fun AccountRoute(
    onNavigateToWelcome: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onNavigateToWelcome()
    }

    AccountScreen(
        state = state,
        onBackClick = onNavigateUp,
        onLoginClick = onNavigateToWelcome,
        onLogoutClick = viewModel::onLogoutClick,
    )
}
