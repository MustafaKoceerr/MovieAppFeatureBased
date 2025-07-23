package com.mustafakocer.feature_movies.shared.presentation.components.commonpagination

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.mustafakocer.core_domain.exception.toAppException
import com.mustafakocer.core_ui.component.error.ErrorScreen
import com.mustafakocer.core_ui.component.error.toErrorInfo
//import com.mustafakocer.core_ui.component.loading.LoadingScreen

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
// Ana yenileme (refresh) durumunu kontrol et.
    when (val refreshState = lazyPagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            // İlk yükleme veya tam ekran yenileme sırasında gösterilir.
//            LoadingScreen(modifier = modifier)
        }

        is LoadState.Error -> {
            // İlk yükleme veya tam ekran yenileme sırasında bir hata oluştuğunda gösterilir.
            val appException = refreshState.error.toAppException()
            ErrorScreen(
                modifier = modifier,
                error = appException.toErrorInfo(),
                onRetry = { lazyPagingItems.retry() } // Paging 3'ün yerleşik yeniden deneme mekanizması.
            )
        }

        is LoadState.NotLoading -> {
            // Veri başarıyla yüklendiğinde veya yükleme durumu olmadığında
            // dışarıdan sağlanan asıl içeriği göster.
            content(lazyPagingItems)
        }
    }
}