package com.mustafakocer.feature_movies.shared.presentation.components.commonpagination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.mustafakocer.feature_movies.shared.domain.model.MovieList

@Composable
fun MovieListContent(
    movies: LazyPagingItems<MovieList>,
    onMovieClick: (MovieList) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Filmleri listele
        items(
            count = movies.itemCount,
            key = movies.itemKey { it.id }
        ) { index ->
            movies[index]?.let { movie ->
                // ✅ ŞU ŞEKİLDE:
                MovieListItem(
                    movie = movie,
                    onClick = { onMovieClick(movie) },
                    posterSize = PosterSize.Medium,       // List için orta boy
                    maxOverviewLines = 3,                 // Daha fazla satır
                    showVoteCount = true,                 // Oy sayısını göster
                    elevation = 4.dp                      // Normal gölge
                )
            }
        }

        // Paging durumlarını yönet
        item {
            PagingLoadStateIndicator(
                loadState = movies.loadState.refresh,
                onRetry = { movies.retry() }
            )
        }

        item {
            PagingLoadStateIndicator(
                loadState = movies.loadState.append,
                onRetry = { movies.retry() }
            )
        }
    }
}
