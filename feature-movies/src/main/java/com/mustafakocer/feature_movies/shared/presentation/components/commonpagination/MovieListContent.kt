package com.mustafakocer.feature_movies.shared.presentation.components.commonpagination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.loading.LoadingScreen
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import com.mustafakocer.feature_movies.R
/**
 * Paging 3 ile gelen film listesini ve yükleme/hata durumlarını yöneten
 * yeniden kullanılabilir bir Composable.
 *
 * @param lazyMovieItems UI katmanında .collectAsLazyPagingItems() ile oluşturulan Paging listesi.
 * @param onMovieClick Bir film öğesine tıklandığında tetiklenecek olay.
 */
@Composable
fun MovieListContent(
    lazyMovieItems: LazyPagingItems<MovieListItem>,
    onMovieClick: (MovieListItem) -> Unit,
) {
    // Paging'in ana yükleme durumunu (ilk yükleme veya tam ekran yenileme) kontrol ediyoruz.
    when (lazyMovieItems.loadState.refresh) {
        // 1. TAM EKRAN YÜKLEME DURUMU
        is LoadState.Loading -> {
            LoadingScreen(message = stringResource(R.string.loading_movies))
        }
        // 2. TAM EKRAN HATA DURUMU
        is LoadState.Error -> {
            ErrorScreen(
                onRetry = { lazyMovieItems.retry() }
            )
        }
        // 3. BAŞARILI VEYA BOŞ DURUM
        is LoadState.NotLoading -> {
            if (lazyMovieItems.itemCount == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                }
            } else {
                // Veri varsa, LazyColumn'u gösteriyoruz.
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        count = lazyMovieItems.itemCount,
                        key = lazyMovieItems.itemKey { it.id }
                    ) { index ->
                        lazyMovieItems[index]?.let { movie ->
                            MovieListItem(
                                movie = movie,
                                onClick = { onMovieClick(movie) }
                            )
                        }
                    }

                    // SAYFA SONU YÜKLEME VE HATA DURUMLARI
                    when (lazyMovieItems.loadState.append) {
                        is LoadState.Loading -> {
                            // Sayfa sonunda dönen küçük spinner
                            item { PagingAppendIndicator() }
                        }
                        is LoadState.Error -> {
                            // Sayfa sonunda çıkan "Tekrar Dene" butonu
                            item { PagingAppendErrorIndicator(onRetry = { lazyMovieItems.retry() }) }
                        }
                        is LoadState.NotLoading -> {
                            // Hiçbir şey yapma
                        }
                    }
                }
            }
        }
    }
}
