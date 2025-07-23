package com.mustafakocer.feature_movies.list.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.feature_movies.R

/**
 * A custom TopAppBar designed for the movie list screen.
 *
 * It displays a dynamic title and a navigation icon to go back.
 *
 * @param title The text to be displayed as the title of the app bar.
 * @param onNavigateBack The callback function to be invoked when the navigation icon is clicked.
 * @param modifier The modifier to be applied to the TopAppBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListTopAppBar(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.bounceClick()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        // UI/UX Decision: The app bar is transparent by default, allowing content to scroll
        // behind it. When the user scrolls down, `scrolledContainerColor` applies a semi-opaque
        // background. This creates a modern, layered effect while ensuring the app bar controls
        // remain legible as content moves underneath.
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
        )
    )
}