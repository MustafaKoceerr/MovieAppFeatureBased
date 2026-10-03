package com.mustafakocer.movieappfeaturebasedclean.feature.details.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.movieappfeaturebasedclean.R

/**
 * A specialized Floating Action Button for triggering a share action.
 *
 * @param onClick A lambda to be invoked when the button is clicked.
 */
@Composable
fun ShareFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.bounceClick(),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = stringResource(R.string.share_movie),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}
