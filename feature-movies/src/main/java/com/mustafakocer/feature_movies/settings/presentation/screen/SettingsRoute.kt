package com.mustafakocer.feature_movies.settings.presentation.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.core_ui.component.error.toErrorInfo
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEffect
import com.mustafakocer.feature_movies.settings.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material3.SnackbarDuration
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEvent
import com.mustafakocer.navigation_contracts.actions.movies.SettingsNavActions

/**
 * Host (Barındırıcı) Eylemleri: Bunlar, bir özelliğin (feature) kendi başına yapamayacağı, sadece tüm uygulamanın sahibi olan Activity gibi bir üst bileşenin yapabileceği çok nadir ve özel eylemlerdir.
 *
 *     activity.recreate() (Dil/Tema değişikliği)
 *
 *     Sistem izni istemek (requestPermissions)
 *
 *     Sistem ayarları ekranını açmak
 */
// Dosya: settings/presentation/screen/SettingsRoute.kt

@Composable
fun SettingsRoute(
    navActions: SettingsNavActions,
    onLanguageChanged: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Route'un görevi artık sadece Effect'leri dinlemek.
    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is SettingsEffect.NavigateBack -> navActions.navigateUp()
                is SettingsEffect.RestartActivity -> onLanguageChanged()
            }
        }
    }

    // Hata gösterme mantığı artık Screen'in kendi içinde.
    SettingsScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState
    )
}