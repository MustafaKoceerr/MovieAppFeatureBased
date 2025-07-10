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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mustafakocer.core_common.presentation.UiContract
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.feature_movies.settings.presentation.component.ComingSoonSection
import com.mustafakocer.feature_movies.settings.presentation.component.SettingsHeader
import com.mustafakocer.feature_movies.settings.presentation.component.SettingsTopBar
import com.mustafakocer.feature_movies.settings.presentation.component.ThemeSelectionSection
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEffect
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsEvent
import com.mustafakocer.feature_movies.settings.presentation.contract.SettingsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    contract: UiContract<SettingsUiState, SettingsEvent, SettingsEffect>,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() } // remember eklemek iyi bir pratik
) {
    val state by contract.uiState.collectAsStateWithLifecycle()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SettingsTopBar(
                onBackPressed = { contract.onEvent(SettingsEvent.BackPressed) }
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
                contract.onEvent(SettingsEvent.ThemeSelected(theme))
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
    onThemeSelected: (ThemePreference) -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Başlık Bölümü
        SettingsHeader()

        // 2. Tema Seçim Bölümü
        ThemeSelectionSection(
            currentTheme = state.currentTheme,
            isLoading = state.isLoading,
            onThemeSelected = onThemeSelected
        )

        // 3. "Yakında Gelecek" Bölümleri
        ComingSoonSection()

        // Alt boşluk
        Spacer(modifier = Modifier.height(32.dp))
    }
}