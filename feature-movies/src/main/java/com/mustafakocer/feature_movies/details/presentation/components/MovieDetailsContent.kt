package com.mustafakocer.feature_movies.details.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mustafakocer.feature_movies.details.util.fullBackdropUrl
import com.mustafakocer.feature_movies.details.util.fullPosterUrl
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails

/**
 * Ana içerik ve yenileme göstergesi için bir sarmalayıcı (wrapper).
 */
@Composable
fun MovieDetailsContent(
    movie: MovieDetails,
    isRefreshLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            MovieHeroSection(
                backdropUrl = movie.fullBackdropUrl(),
                posterUrl = movie.fullPosterUrl(),
                title = movie.title,
                tagline = if (movie.hasTagline) movie.tagline else null
            )

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MovieStatsSection(
                    voteAverage = movie.voteAverage,
                    releaseDate = movie.releaseDate,
                    runtime = movie.runtime
                )
                if (movie.genres.isNotEmpty()) {
                    MovieGenresSection(genres = movie.genres)
                }
                MovieOverviewSection(overview = movie.overview)
            }

            Spacer(modifier = Modifier.height(80.dp)) // FAB için boşluk
        }

        if (isRefreshLoading) {
            RefreshLoadingIndicator(modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}