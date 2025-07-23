package com.mustafakocer.feature_movies.details.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.util.bounceClick
import com.mustafakocer.feature_movies.R

@Composable
fun ShareFloatingActionButton(isSharing: Boolean, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.bounceClick(),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        if (isSharing) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = stringResource(R.string.share_movie),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}