package com.mustafakocer.core_ui.component.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Compact loading indicator for inline usage
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "Loading...",
    size: LoadingSize = LoadingSize.Medium,
) {
    val indicatorSize = size.size

    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(indicatorSize),
            strokeWidth = 3.dp,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = message,
            style = when (size) {
                LoadingSize.Small -> MaterialTheme.typography.bodySmall
                LoadingSize.Medium -> MaterialTheme.typography.bodyMedium
                LoadingSize.Large -> MaterialTheme.typography.bodyLarge
            },
            color = MaterialTheme.colorScheme.onSurface
        )
    }

}