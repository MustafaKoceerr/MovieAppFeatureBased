package com.mustafakocer.feature_auth.welcome.presentation.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_auth.welcome.presentation.viewmodel.WelcomeViewModel
import com.mustafakocer.feature_auth.welcome.presentation.contract.WelcomeEffect
import androidx.compose.runtime.getValue
import com.mustafakocer.navigation_contracts.actions.auth.WelcomeNavActions
import androidx.core.net.toUri


@Composable
fun WelcomeRoute(
    navActions: WelcomeNavActions,
    viewModel: WelcomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Effect'leri dinle.
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is WelcomeEffect.NavigateToHome -> {
                    navActions.navigateToHome()
                }

                is WelcomeEffect.NavigateToTmdbLogin -> {
                    // Cihazın varsayılan internet tarayıcısını açmak için bir intent oluştur.
                    val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                    context.startActivity(intent)
                }
            }
        }
    }

    WelcomeScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )

}