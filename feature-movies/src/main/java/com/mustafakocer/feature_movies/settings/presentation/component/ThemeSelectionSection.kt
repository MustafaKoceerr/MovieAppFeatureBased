package com.mustafakocer.feature_movies.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_preferences.models.ThemePreference

/**
 * Tema seçimi için kullanılan ana bölüm kartı.
 * Potansiyel olarak kendi dosyasına taşınabilir: ThemeSelectionSection.kt
 */
@Composable
fun ThemeSelectionSection(
    currentTheme: ThemePreference,
    isLoading: Boolean,
    onThemeSelected: (ThemePreference) -> Unit
) {
    SectionCard(
        icon = Icons.Default.Palette,
        title = "App Theme",
        isLoading = isLoading
    ) {
        // Tema seçenekleri bu kartın içeriği olarak yerleştirilir.
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeOption(
                theme = ThemePreference.LIGHT,
                icon = Icons.Default.LightMode,
                description = "Always use light theme",
                isSelected = currentTheme == ThemePreference.LIGHT,
                isEnabled = !isLoading,
                onSelected = { onThemeSelected(ThemePreference.LIGHT) }
            )
            ThemeOption(
                theme = ThemePreference.DARK,
                icon = Icons.Default.DarkMode,
                description = "Always use dark theme",
                isSelected = currentTheme == ThemePreference.DARK,
                isEnabled = !isLoading,
                onSelected = { onThemeSelected(ThemePreference.DARK) }
            )
            ThemeOption(
                theme = ThemePreference.SYSTEM,
                icon = Icons.Default.Settings,
                description = "Follow system setting",
                isSelected = currentTheme == ThemePreference.SYSTEM,
                isEnabled = !isLoading,
                onSelected = { onThemeSelected(ThemePreference.SYSTEM) }
            )
        }
    }
}