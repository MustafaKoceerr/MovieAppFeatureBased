package com.mustafakocer.feature_movies.settings.presentation.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_ui.component.error.toErrorInfo
import com.mustafakocer.feature_movies.settings.presentation.component.ComingSoonSection
import com.mustafakocer.feature_movies.settings.presentation.component.LanguageSelectionSection
import com.mustafakocer.feature_movies.settings.presentation.component.SettingsHeader
import com.mustafakocer.feature_movies.settings.presentation.component.SettingsTopBar
import com.mustafakocer.feature_movies.settings.presentation.component.ThemeSelectionSection
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEvent
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onEvent: (SettingsEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {

    state.error?.let { error ->
        val errorInfo = error.toErrorInfo()
        val message = "${errorInfo.title}: ${errorInfo.description}"

        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            // Hata gösterildikten sonra, state'i temizle.
            onEvent(SettingsEvent.DismissError)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SettingsTopBar(
                onBackPressed = { onEvent(SettingsEvent.BackClicked) }
            )
        }
    ) { paddingValues ->
        // Ana içerik listesi
        SettingsContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = state,
            onThemeSelected = { theme ->
                onEvent(SettingsEvent.ThemeSelected(theme))
            },
            onLanguageSelected = { language ->
                onEvent(SettingsEvent.LanguageSelected(language))
            }
        )
    }
}

/**
 * Ayarlar ekranının kaydırılabilir ana içeriğini düzenler.
 */
@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    state: SettingsUiState,
    onThemeSelected: (ThemePreference) -> Unit,
    onLanguageSelected: (LanguagePreference) -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingsHeader()

        ThemeSelectionSection(
            currentTheme = state.currentTheme,
            // Tema değiştirme işlemi sırasında butonları devre dışı bırakmak veya
            // bir yükleme göstergesi göstermek için bu state'i kullanabiliriz.
            isLoading = state.isLoading,
            onThemeSelected = onThemeSelected
        )

        // Yeni language selection
        LanguageSelectionSection(
            currentLanguage = state.currentLanguage,
            isLoading = state.isLoading,
            onLanguageSelected = onLanguageSelected
        )

        ComingSoonSection()

        Spacer(modifier = Modifier.height(32.dp))
    }
}