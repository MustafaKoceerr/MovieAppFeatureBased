package com.mustafakocer.feature_movies.settings.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A reusable card component that provides a consistent layout for sections on the settings screen.
 *
 * Architectural Decision: This component is a prime example of creating a reusable UI building block.
 * Instead of styling each settings section (e.g., Theme, Language) individually, this `SectionCard`
 * enforces a consistent visual structure (icon, title, padding, elevation), which promotes a cohesive
 * UI and significantly reduces code duplication.
 *
 * @param icon The [ImageVector] to be displayed in the section header.
 * @param title The title of the section.
 * @param isLoading A boolean flag that, when true, displays a progress indicator in the header.
 *                  This is useful for providing visual feedback during asynchronous operations.
 * @param modifier The modifier to be applied to the Card.
 * @param content A composable lambda (slot) where the specific content for this section (e.g.,
 *                a list of options) can be placed. This "slot API" pattern makes the `SectionCard`
 *                a flexible and powerful container.
 */
@Composable
fun SectionCard(
    icon: ImageVector,
    title: String,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        // A subtle border helps the card stand out from the background, especially on surfaces
        // with similar colors.
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header row for the icon and title.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null, // Decorative icon
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                // UI/UX Decision: The loading indicator is displayed conditionally. The `Spacer` with
                // `weight(1f)` is a clever layout trick that pushes the indicator to the far
                // right end of the row, keeping the title aligned to the left.
                if (isLoading) {
                    Spacer(modifier = Modifier.weight(1f))
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                }
            }
            // The slot for the section's main content is placed here.
            content()
        }
    }
}