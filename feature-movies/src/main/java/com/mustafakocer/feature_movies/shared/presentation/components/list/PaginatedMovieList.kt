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
import androidx.compose.animation.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import kotlinx.coroutines.launch

/**
 * Paging 3 ile gelen film listesini ve "Yukarı Çık" butonunu gösteren ana Composable.
 */
@Composable
fun PaginatedMovieList(
    lazyPagingItems: LazyPagingItems<MovieListItem>,
    onMovieClick: (MovieListItem) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Butonun görünürlüğünü performanslı bir şekilde hesapla.
    // Sadece boolean sonuç değiştiğinde recompose olur.
    val showScrollToTopButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 5 // 5. öğeyi geçince butonu göster
        }
    }

    Box(modifier = modifier.padding(contentPadding)) {
        LazyColumn(
            state = listState, // State'i LazyColumn'a bağla
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Liste boşsa gösterilecek olan öğe
            if (lazyPagingItems.loadState.refresh is LoadState.NotLoading && lazyPagingItems.itemCount == 0) {
                item {
                    EmptyListIndicator(modifier = Modifier.fillParentMaxSize())
                }
            }

            // Film öğeleri
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

            // Sayfa sonu yükleme/hata göstergesi
            item {
                PagingAppendIndicator(lazyPagingItems = lazyPagingItems)
            }
        }

        // "Yukarı Çık" Butonu
        ScrollToTopButton(
            isVisible = showScrollToTopButton,
            onClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0) // Animasyonla en üste kaydır
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

// ... (MovieRow, MovieInfo, PagingAppendIndicator, EmptyListIndicator fonksiyonları aynı kalacak)

/**
 * Animasyonlu bir şekilde görünüp kaybolan "Yukarı Çık" FloatingActionButton'u.
 */
@Composable
private fun ScrollToTopButton(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
        modifier = modifier
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(R.string.scroll_to_top)
            )
        }
    }
}

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
        is LoadState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
            }
        }

        is LoadState.Error -> {
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