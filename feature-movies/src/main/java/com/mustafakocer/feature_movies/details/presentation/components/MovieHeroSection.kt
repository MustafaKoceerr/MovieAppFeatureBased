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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

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
    isOffline: Boolean,
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
            contentDescription = "$title backdrop",
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
            isOffline = isOffline,
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
    isOffline: Boolean,
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
            contentDescription = "$title poster",
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

            // Sadece offline ise gösterilir
            if (isOffline) {
                OfflineIndicator(modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

/**
 * İçeriğin çevrimdışı olduğunu belirten küçük bir gösterge.
 */
@Composable
private fun OfflineIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = "Offline content",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "Offline content",
            style = MaterialTheme.typography.labelMedium, // Daha uygun bir stil
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}