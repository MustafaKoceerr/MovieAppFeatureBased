package com.mustafakocer.feature_movies.details.presentation.components

/**
 * Filmin türlerini (genre) AssistChip'ler kullanarak gösteren bölüm.
 * FlowRow sayesinde farklı ekran boyutlarına uyum sağlar.
 */
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mustafakocer.feature_movies.details.domain.model.Genre

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieGenresSection(
    genres: List<Genre>, // Genre modelini import etmeyi unutma
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Genres",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            genres.forEach { genre ->
                AssistChip(
                    // TODO: Türlere tıklandığında ilgili filmleri listeleyen
                    // bir ekrana gitmek için event tetiklenebilir.
                    onClick = { /* onGenreClick(genre.id) */ },
                    label = {
                        Text(
                            text = genre.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )
            }
        }
    }
}