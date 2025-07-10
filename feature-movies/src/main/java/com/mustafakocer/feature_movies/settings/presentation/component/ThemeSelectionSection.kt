package com.mustafakocer.feature_movies.settings.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_preferences.models.ThemePreference

@Composable
fun ThemeSelectionSection(
    currentTheme: ThemePreference,
    isLoading: Boolean,
    onThemeSelected: (ThemePreference) -> Unit,
) {
    SectionCard(
        icon = Icons.Default.Palette,
        title = "App Theme",
        isLoading = isLoading
    ) {
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

@Composable
private fun ThemeOption(
    theme: ThemePreference,
    icon: ImageVector,
    description: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onSelected: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .selectable(
                selected = isSelected,
                onClick = onSelected,
                enabled = isEnabled,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent,
        border = if (isSelected) BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.5f
                ),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = icon,
                        contentDescription = theme.displayName,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = theme.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(300))
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            RadioButton(
                selected = isSelected,
                onClick = null,
                enabled = isEnabled,
                modifier = Modifier.size(0.dp)
            )
        }
    }
}
