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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_preferences.models.ThemePreference
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.feature_movies.R

/**
 * A composable that presents the available theme choices (e.g., Light, Dark, System)
 * within a styled section card.
 *
 * @param currentTheme The currently active [ThemePreference] to highlight the selected option.
 * @param isLoading A boolean to indicate if a preference change is in progress, used to disable controls.
 * @param onThemeSelected The callback invoked when a user selects a new theme.
 */
@Composable
fun ThemeSelectionSection(
    currentTheme: ThemePreference,
    isLoading: Boolean,
    onThemeSelected: (ThemePreference) -> Unit,
) {
    // `SectionCard` is a reusable wrapper that provides a consistent look and feel
    // for different sections on the settings screen.
    SectionCard(
        icon = Icons.Default.Palette,
        title = stringResource(R.string.app_theme),
        isLoading = isLoading
    ) {
        // Architectural Decision: The `selectableGroup` modifier is crucial for accessibility.
        // It groups the individual `ThemeOption` composables together, allowing screen readers
        // to understand that they form a single set of mutually exclusive choices, similar to a
        // traditional radio button group.
        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeOption(
                theme = ThemePreference.LIGHT,
                icon = Icons.Default.LightMode,
                description = stringResource(R.string.theme_light_always),
                isSelected = currentTheme == ThemePreference.LIGHT,
                isEnabled = !isLoading,
                onSelected = { onThemeSelected(ThemePreference.LIGHT) }
            )
            ThemeOption(
                theme = ThemePreference.DARK,
                icon = Icons.Default.DarkMode,
                description = stringResource(R.string.theme_dark_always),
                isSelected = currentTheme == ThemePreference.DARK,
                isEnabled = !isLoading,
                onSelected = { onThemeSelected(ThemePreference.DARK) }
            )
            ThemeOption(
                theme = ThemePreference.SYSTEM,
                icon = Icons.Default.Settings,
                description = stringResource(R.string.theme_follow_system),
                isSelected = currentTheme == ThemePreference.SYSTEM,
                isEnabled = !isLoading,
                onSelected = { onThemeSelected(ThemePreference.SYSTEM) }
            )
        }
    }
}

/**
 * A private composable that renders a single, selectable row for a theme option.
 *
 * It provides strong visual feedback for the selected state and is designed with accessibility in mind.
 *
 * @param theme The [ThemePreference] this option represents.
 * @param icon The icon associated with this theme.
 * @param description A short description of the theme option.
 * @param isSelected Whether this option is the currently selected one.
 * @param isEnabled Whether this option is enabled and can be interacted with.
 * @param onSelected The callback to be invoked when this option is clicked.
 */
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
            .bounceClick()
            // Architectural Decision: The `selectable` modifier is used to make the entire row
            // clickable and to report its state to accessibility services. Setting the `role` to
            // `Role.RadioButton` tells screen readers to treat this custom composable as a
            // standard radio button, even though we are using custom visuals.
            .selectable(
                selected = isSelected,
                onClick = onSelected,
                enabled = isEnabled,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(12.dp),
        // UI/UX Decision: The background color and border change distinctly when an item is
        // selected, providing clear and immediate visual feedback to the user.
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else Color.Transparent,
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null, // Decorative icon
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
            // UI/UX Decision: The checkmark icon fades in and out smoothly instead of just
            // appearing, which creates a more polished and less jarring user experience.
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
                            contentDescription = stringResource(R.string.selected_text),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            // Accessibility Trick: A zero-sized `RadioButton` is included. It is not visible to the
            // user, but its presence, combined with the `selectable` modifier's role, provides the
            // necessary semantics for accessibility services like TalkBack to correctly announce
            // the state ("Selected" / "Not selected") of the custom component.
            RadioButton(
                selected = isSelected,
                onClick = null, // The `selectable` modifier on the parent handles the click.
                enabled = isEnabled,
                modifier = Modifier.size(0.dp)
            )
        }
    }
}