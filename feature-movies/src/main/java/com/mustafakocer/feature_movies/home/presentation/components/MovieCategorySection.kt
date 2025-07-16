package com.mustafakocer.feature_movies.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem

/**
 * Ana ekranda tek bir film kategorisini (örn. "Popüler Filmler") ve
 * filmlerini yatay bir listede gösteren, yeniden kullanılabilir Composable.
 *
 * @param category Gösterilecek kategorinin enum'u (başlık için kullanılır).
 * @param movies O kategoriye ait film listesi.
 * @param onMovieClick Bir filme tıklandığında tetiklenecek olay. Filmin ID'sini döndürür.
 * @param onViewAllClick "Hepsini Gör" butonuna tıklandığında tetiklenecek olay.
 */
@Composable
fun MovieCategorySection(
    categoryTitle: String,
    movies: List<MovieListItem>,
    onMovieClick: (movieId: Int) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Liste boşsa, bu bölümü hiç gösterme.
    if (movies.isEmpty()) return

    Column(modifier = modifier) {
        // Başlık ve "Hepsini Gör" Butonu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryTitle, // MovieCategory enum'unun bir 'title' alanı olduğunu varsayıyoruz.
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onViewAllClick) {
                Text(stringResource(R.string.view_all))
            }
        }

        // Filmlerin Yatay Listesi (LazyRow)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = movies,
                key = { movie -> movie.id } // Performans için her öğeye benzersiz bir anahtar veriyoruz.
            ) { movie ->
                MovieCard(
                    movie = movie,
                    onClick = { onMovieClick(movie.id) }
                )
            }
        }
    }
}