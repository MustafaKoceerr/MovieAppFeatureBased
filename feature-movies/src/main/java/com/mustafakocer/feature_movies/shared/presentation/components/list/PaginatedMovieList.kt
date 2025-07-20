package com.mustafakocer.feature_movies.shared.presentation.components.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import com.mustafakocer.feature_movies.shared.presentation.components.atoms.MoviePoster
import com.mustafakocer.feature_movies.shared.presentation.components.atoms.PosterSize
import com.mustafakocer.feature_movies.shared.util.formattedRating

/**
 * Paging 3 ile gelen film listesini ve sayfa sonu yükleme/hata durumlarını
 * gösteren, dışarıya açılan ana Composable.
 *
 * Bu bileşen, listenin kendisini ve sayfa sonu göstergelerini yönetir.
 * Tam ekran yükleme/hata durumları için `HandlePagingLoadState` ile sarmalanmalıdır.
 *
 * @param lazyMovieItems UI katmanında .collectAsLazyPagingItems() ile oluşturulan Paging listesi.
 * @param onMovieClick Bir film öğesine tıklandığında tetiklenecek olay.
 * @param modifier Bu bileşene uygulanacak olan Modifier.
 * @param contentPadding Scaffold gibi üst bileşenlerden gelen iç boşluk.
 */

// ... (diğer importlar aynı kalacak)
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.paging.LoadState

@Composable
fun PaginatedMovieList(
    lazyPagingItems: LazyPagingItems<MovieListItem>,
    onMovieClick: (MovieListItem) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    // Dışarıdan gelen `contentPadding`'i (örn. Scaffold'dan gelen) en dıştaki Box'a uygula.
    Box(modifier = modifier.padding(contentPadding)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            // LazyColumn'un kendi iç padding'i.
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Liste içeriği
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id }
            ) { index ->
                lazyPagingItems[index]?.let { movie ->
                    MovieRow(
                        movie = movie,
                        onClick = { onMovieClick(movie) }
                    )
                }
            }

            // Sayfa sonu göstergeleri
            item {
                PagingAppendIndicator(lazyPagingItems = lazyPagingItems)
            }

            // Yükleme bittiğinde ve liste tamamen boşsa, boş liste göstergesini göster.
            if (lazyPagingItems.loadState.refresh is LoadState.NotLoading && lazyPagingItems.itemCount == 0) {
                item {
                    // EmptyListIndicator'ı LazyColumn'un bir öğesi olarak tam ekran kaplayacak şekilde ekliyoruz.
                    EmptyListIndicator(
                        modifier = Modifier
                            .fillParentMaxSize() // LazyColumn'un tüm alanını kapla
                            .padding(bottom = 100.dp) // Alttaki append indicator ile çakışmasın diye biraz boşluk
                    )
                }
            }
        }
    }
}

// ... (dosyanın geri kalanı aynı)
// ... (dosyanın geri kalanı aynı)
// ---------- İÇ IMPLEMENTASYON (PRIVATE) ----------

@Composable
private fun MovieRow(
    movie: MovieListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoviePoster(
                posterPath = movie.posterUrl,
                contentDescription = movie.title,
                size = PosterSize.Medium
            )
            MovieInfo(
                movie = movie,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MovieInfo(
    movie: MovieListItem,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = movie.releaseYear,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = movie.overview,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⭐ ${movie.voteAverage.formattedRating}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.votes, movie.voteCount),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PagingAppendIndicator(
    lazyPagingItems: LazyPagingItems<*>,
    modifier: Modifier = Modifier,
) {
    when (lazyPagingItems.loadState.append) {
        is androidx.paging.LoadState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
            }
        }

        is androidx.paging.LoadState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.error_loading_more),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Button(onClick = { lazyPagingItems.retry() }) {
                    Text(stringResource(R.string.try_again))
                }
            }
        }

        is androidx.paging.LoadState.NotLoading -> {
            // Hiçbir şey yapma
        }
    }
}

@Composable
private fun EmptyListIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_movies_found),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}