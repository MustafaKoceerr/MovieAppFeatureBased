package com.mustafakocer.feature_movies.details.presentation.components

import androidx.compose.runtime.Composable
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import androidx.compose.ui.res.stringResource
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.shared.util.formattedRating

@Composable
fun formatShareContent(movie: MovieDetails): String {
    val shareTitle = stringResource(R.string.share_text_title)
    val textRating = stringResource(R.string.share_text_rating)
    val textRelease = stringResource(R.string.share_text_release)
    val textRuntime = stringResource(R.string.share_text_runtime)
    val textGenres = stringResource(R.string.share_text_genres)
    val textTags = stringResource(R.string.share_text_tags)

    return buildString {
        append("$shareTitle\n\n")
        append("🎬 ${movie.title}\n\n")
        if (movie.hasTagline) {
            append("\"${movie.tagline}\"\n\n")
        }
        append("⭐ $textRating: ${movie.voteAverage.formattedRating}/10\n")
        append("📅 $textRelease: ${movie.releaseDate}\n")
        if (movie.runtime.isNotEmpty()) {
            append("⏱️ $textRuntime: ${movie.runtime}\n")
        }
        if (movie.genres.isNotEmpty()) {
            append("🎭 $textGenres: ${movie.genres.joinToString { it.name }}\n")
        }
        append("\n📖 ${movie.overview}")
        append("\n\n$textTags #${movie.title.replace(" ", "")}")
    }
}