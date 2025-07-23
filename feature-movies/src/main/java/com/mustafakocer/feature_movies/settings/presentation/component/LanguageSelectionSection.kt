package com.mustafakocer.feature_movies.settings.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.feature_movies.R

/**
 * A composable that presents the available language choices within a styled section card.
 *
 * @param currentLanguage The currently active [LanguagePreference] to display in the dropdown field.
 * @param isLoading A boolean to indicate if a preference change is in progress, used to disable controls.
 * @param onLanguageSelected The callback invoked when a user selects a new language.
 * @param modifier The modifier to be applied to the SectionCard.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionSection(
    currentLanguage: LanguagePreference,
    isLoading: Boolean,
    onLanguageSelected: (LanguagePreference) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    SectionCard(
        icon = Icons.Default.Language,
        title = "Language",
        isLoading = isLoading,
        modifier = modifier
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            // The dropdown can only be expanded if not in a loading state.
            onExpandedChange = { expanded = !expanded && !isLoading },
            modifier = Modifier.fillMaxWidth()
        ) {
            // Architectural Decision: The `OutlinedTextField` is set to `readOnly`. This is a key
            // part of the `ExposedDropdownMenuBox` pattern. It prevents the user from typing
            // directly into the field, ensuring that the value can only be changed by selecting
            // an item from the dropdown menu.
            OutlinedTextField(
                value = currentLanguage.displayName,
                onValueChange = {}, // No-op because it's read-only.
                readOnly = true,
                enabled = !isLoading,
                leadingIcon = {
                    FlagIcon(
                        flagResource = currentLanguage.flagResourceName,
                        contentDescription = currentLanguage.displayName
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    // This modifier is essential for correctly anchoring the dropdown menu to the text field.
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .bounceClick()
            )

            // The `DropdownMenu` itself, which is conditionally displayed based on the `expanded` state.
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.exposedDropdownSize() // Ensures the menu has the same width as the text field.
            ) {
                // Iterate through all available languages to create the menu items.
                LanguagePreference.getAllLanguages().forEach { language ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                FlagIcon(
                                    flagResource = language.flagResourceName,
                                    contentDescription = language.displayName
                                )
                                Text(language.displayName)
                            }
                        },
                        onClick = {
                            onLanguageSelected(language)
                            expanded = false // Close the menu after selection.
                        }
                    )
                }
            }
        }
    }
}

/**
 * A private helper composable to display a flag icon based on a string resource name.
 *
 * @param flagResource The string identifier for the flag drawable (e.g., "flag_us").
 * @param contentDescription The content description for accessibility.
 * @param modifier The modifier to be applied to the Icon.
 */
@Composable
private fun FlagIcon(
    flagResource: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    // This `when` statement maps the string identifier from the domain model to a concrete
    // drawable resource ID in the feature module.
    val drawableRes = when (flagResource) {
        "flag_us" -> R.drawable.flag_us
        "flag_tr" -> R.drawable.flag_tr
        "flag_fr" -> R.drawable.flag_fr
        "flag_de" -> R.drawable.flag_de
        "flag_es" -> R.drawable.flag_es
        "flag_it" -> R.drawable.flag_it
        "flag_ru" -> R.drawable.flag_ru
        else -> null
    }

    if (drawableRes != null) {
        Icon(
            painter = painterResource(drawableRes),
            contentDescription = contentDescription,
            modifier = modifier.size(24.dp),
            tint = Color.Unspecified
        )
    } else {
        // A fallback icon is provided in case the resource name is not recognized.
        Icon(
            imageVector = Icons.Default.Language,
            contentDescription = contentDescription,
            modifier = modifier.size(24.dp)
        )
    }
}