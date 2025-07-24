package com.mustafakocer.feature_movies.details.presentation.components

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
import androidx.compose.ui.text.style.TextOverflow
import com.mustafakocer.feature_movies.R

/**
 * A specialized TopAppBar for the Movie Details screen.
 *
 * @param title The title to display in the app bar.
 * @param onNavigateBack A lambda to be invoked when the back navigation icon is clicked.
 * @param modifier The modifier to be applied to the component.
 *
 * Architectural Note:
 * This component encapsulates the specific styling and behavior of the top app bar for the
 * details screen.
 * - **Reusability:** Creating a dedicated component simplifies the main `MovieDetailsScreen` layout
 *   and ensures a consistent appearance.
 * - **Transparent on Scroll:** The `colors` are configured to be transparent by default, allowing
 *   the backdrop image to show through. When the user scrolls, the `scrolledContainerColor`
 *   provides a semi-opaque background, ensuring the title remains readable over the content.
 *   This is a common and polished UX pattern for screens with large header images.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsTopBar(
    title: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.navigate_back)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
        )
    )
}