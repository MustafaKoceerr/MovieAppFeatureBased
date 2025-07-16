package com.mustafakocer.feature_movies.details.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mustafakocer.feature_movies.R

/**
 * Filmin en üstteki "kahraman" bölümünü oluşturur.
 * Backdrop, poster ve temel başlık bilgilerini içerir.
 * Artık MovieDetails objesine değil, ilkel veri tiplerine bağımlıdır.
 */
@Composable
fun MovieHeroSection(
    backdropUrl: String,
    posterUrl: String,
    title: String,
    tagline: String?, // Nullable, çünkü her filmin tagline'ı olmayabilir
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp) // Yüksekliği sabit veya dinamik yapabilirsin
    ) {
        // 1. Arka plan resmi (Backdrop)
        AsyncImage(
            model = backdropUrl,
            contentDescription = stringResource(R.string.backdrop_description, title),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            // Hata veya yükleme durumları için placeholder ekleyebilirsin
            // placeholder = painterResource(R.drawable.placeholder_backdrop),
            // error = painterResource(R.drawable.placeholder_backdrop)
        )

        // 2. Okunabilirliği artırmak için alttan üste doğru karartma efekti
        GradientOverlay()

        // 3. Poster ve metin bilgilerini içeren katman
        MovieInfoOverlay(
            posterUrl = posterUrl,
            title = title,
            tagline = tagline,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

/**
 * Resmin üzerine eklenen ve metnin okunabilirliğini artıran gradyan katmanı.
 */
@Composable
private fun GradientOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.2f),
                        Color.Black.copy(alpha = 0.8f)
                    ),
                    startY = 300f // Gradyanın nerede başlayacağını ayarlar
                )
            )
    )
}

/**
 * Poster, başlık, tagline ve offline göstergesini içeren bilgi katmanı.
 */
@Composable
private fun MovieInfoOverlay(
    posterUrl: String,
    title: String,
    tagline: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom // Poster ve metni alttan hizalar
    ) {
        // Film afişi
        AsyncImage(
            model = posterUrl,
            contentDescription = stringResource(R.string.poster_description, title),
            modifier = Modifier
                .size(120.dp, 180.dp)
                .clip(MaterialTheme.shapes.medium), // Köşeleri yuvarlatır
            contentScale = ContentScale.Crop
        )

        // Metin bilgileri
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall, // Daha uygun bir stil
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Sadece tagline varsa gösterilir
            tagline?.let {
                Text(
                    text = "\"$it\"",
                    style = MaterialTheme.typography.titleSmall, // Daha uygun bir stil
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
