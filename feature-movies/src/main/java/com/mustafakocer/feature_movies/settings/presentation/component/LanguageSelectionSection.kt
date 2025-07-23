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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mustafakocer.core_preferences.models.LanguagePreference
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.feature_movies.R

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
            onExpandedChange = { expanded = !expanded && !isLoading },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = currentLanguage.displayName,
                onValueChange = {},
                readOnly = true,
                enabled = !isLoading,
                leadingIcon = {
                    FlagIcon(
                        flagResource = currentLanguage.flagResource,
                        contentDescription = currentLanguage.displayName
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .bounceClick()
            )

            if (expanded) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    LanguagePreference.getAllLanguages().forEach { language ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    FlagIcon(
                                        flagResource = language.flagResource,
                                        contentDescription = language.displayName
                                    )
                                    Text(language.displayName)
                                }
                            },
                            onClick = {
                                onLanguageSelected(language)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FlagIcon(
    flagResource: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val drawableRes = when (flagResource) {
        "flag_us" -> R.drawable.flag_us
        "flag_tr" -> R.drawable.flag_tr
        "flag_fr" -> R.drawable.flag_fr // <-- YENİ
        "flag_de" -> R.drawable.flag_de // <-- YENİ
        "flag_es" -> R.drawable.flag_es // <-- YENİ
        "flag_it" -> R.drawable.flag_it // <-- YENİ
        "flag_ru" -> R.drawable.flag_ru // <-- YENİ
        else -> null
    }

    if (drawableRes != null) {
        Icon(
            painter = painterResource(drawableRes),
            contentDescription = contentDescription,
            modifier = modifier.size(24.dp),
            tint = androidx.compose.ui.graphics.Color.Unspecified // SVG için tint kaldır
        )
    } else {
        Icon(
            imageVector = Icons.Default.Language,
            contentDescription = contentDescription,
            modifier = modifier.size(24.dp)
        )
    }
}