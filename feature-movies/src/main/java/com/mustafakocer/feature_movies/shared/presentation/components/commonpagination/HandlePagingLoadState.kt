package com.mustafakocer.feature_movies.shared.presentation.components.commonpagination

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.mustafakocer.core_domain.exception.toAppException
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.error.toErrorInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.mustafakocer.core_ui.component.loading.ShimmerLoadingScreen
import com.mustafakocer.feature_movies.details.presentation.components.MovieDetailsSkeleton
import com.mustafakocer.feature_movies.shared.presentation.components.atoms.MovieListItemSkeleton

/**
 * Paging 3'ün yükleme durumlarını (LoadState) yöneten merkezi ve yeniden kullanılabilir Composable.
 *
 * Bu bileşen, tam ekran yükleme, tam ekran hata ve başarılı içerik durumlarını ele alır.
 *
 * @param T LazyPagingItems'in içerdiği veri tipi.
 * @param lazyPagingItems Durumu yönetilecek olan Paging 3 veri akışı.
 * @param modifier Bu bileşene uygulanacak olan Modifier.
 * @param content Veri başarıyla yüklendiğinde gösterilecek olan Composable içerik.
 *                Bu lambda, `LazyPagingItems<T>`'i parametre olarak alır, böylece
 *                içerik, sayfalanmış veriye erişebilir.
 */

@Composable
fun <T : Any> HandlePagingLoadState(
    lazyPagingItems: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    content: @Composable (LazyPagingItems<T>) -> Unit,
) {
    // Crossfade'li versiyonu buraya entegre ediyoruz.
    val loadState = lazyPagingItems.loadState.refresh
    var showShimmer by remember { mutableStateOf(true) }

    LaunchedEffect(loadState) {
        showShimmer = loadState is LoadState.Loading
    }

    Crossfade(
        targetState = showShimmer,
        animationSpec = tween(durationMillis = 500),
        label = "ContentCrossfade"
    ) { isLoading ->
        if (isLoading) {
            ShimmerLoadingScreen(
                itemHeight = 150.dp,
                itemWidth = 300.dp, // Dikey liste için bu değerin önemi yok
                skeletonContent = { MovieListItemSkeleton() }
            )
        } else {
            if (loadState is LoadState.Error) {
                val appException = loadState.error.toAppException()
                ErrorScreen(
                    modifier = modifier,
                    error = appException.toErrorInfo(),
                    onRetry = { lazyPagingItems.retry() }
                )
            } else {
                content(lazyPagingItems)
            }
        }
    }
}
