package com.mustafakocer.feature_movies.details.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.feature_movies.R

/**
 * A specialized Floating Action Button for triggering a share action.
 *
 * @param isSharing A flag to indicate if the share process is active, showing a progress indicator.
 * @param onClick A lambda to be invoked when the button is clicked.
 */
@Composable
fun ShareFloatingActionButton(isSharing: Boolean, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.bounceClick(),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        if (isSharing) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = stringResource(R.string.share_movie),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}