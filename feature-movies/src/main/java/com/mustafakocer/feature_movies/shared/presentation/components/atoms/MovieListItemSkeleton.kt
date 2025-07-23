package com.mustafakocer.feature_movies.shared.presentation.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.loading.ShimmerBrush


/**
 * PaginatedMovieList'teki bir satırın (MovieRow) yükleniyor halini
 * temsil eden iskelet (skeleton) Composable'ı.
 */
@Composable
fun MovieListItemSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp), // MovieRow ile aynı padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Poster iskeleti
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 120.dp) // MoviePoster (Medium) ile aynı boyut
                .clip(RoundedCornerShape(8.dp))
                .background(ShimmerBrush())
        )
        Spacer(modifier = Modifier.width(16.dp)) // MovieRow ile aynı boşluk
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { // Boşlukları artırdık
            // Başlık iskeleti
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ShimmerBrush())
            )
            // Tarih iskeleti
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.4f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ShimmerBrush())
            )
            // Özet iskeleti
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ShimmerBrush())
            )
        }
    }
}