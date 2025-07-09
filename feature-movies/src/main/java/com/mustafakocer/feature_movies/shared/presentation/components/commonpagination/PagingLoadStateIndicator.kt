package com.mustafakocer.feature_movies.shared.presentation.components.commonpagination

/**
 * Paging3'ün yükleme ve hata durumlarını yöneten genel bir bileşen.
 */
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_ui.component.error.GenericErrorMessageFactory
import com.mustafakocer.core_ui.component.error.toErrorInfoOrFallback

@Composable
fun PagingLoadStateIndicator(
    loadState: LoadState,
    onRetry: () -> Unit,
) {
    when (loadState) {
        is LoadState.Loading -> {
            LoadingIndicator(modifier = Modifier.fillMaxWidth())
        }

        is LoadState.Error -> {
            val errorInfo = (loadState.error as? AppException)?.toErrorInfoOrFallback()
                ?: GenericErrorMessageFactory.unknownError()
            PaginationErrorItem(
                message = errorInfo.description,
                onRetry = onRetry,
                modifier = Modifier.fillMaxWidth()
            )
        }

        is LoadState.NotLoading -> { /* Hiçbir şey yapma */
        }
    }
}