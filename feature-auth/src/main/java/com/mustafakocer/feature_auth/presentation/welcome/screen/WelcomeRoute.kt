package com.mustafakocer.feature_auth.presentation.welcome.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.feature_auth.presentation.welcome.viewmodel.WelcomeViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.mustafakocer.feature_auth.presentation.welcome.contract.WelcomeEffect
import com.mustafakocer.navigation_contracts.actions.FeatureAuthNavActions


@Composable
fun WelcomeRoute(
    navActions: FeatureAuthNavActions,
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
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(effect.url))
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