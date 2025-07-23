package com.mustafakocer.feature_movies.details.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mustafakocer.feature_movies.R
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import com.mustafakocer.feature_movies.shared.util.formattedRating

/**
 * Formats `MovieDetails` into a user-friendly, shareable plain text string.
 *
 * @param movie The movie details to format.
 * @return A formatted `String` ready to be used in a share intent.
 *
 * Architectural Note:
 * This is a `@Composable` function because it needs access to the composable context to resolve
 * localized string resources (`stringResource`, `getQuantityString`). This is a deliberate
 * design choice that allows the shared text to be correctly localized according to the user's
 * device settings, while keeping the ViewModel completely free of Android `Context` dependencies.
 * Centralizing this logic here ensures a consistent share message format is used everywhere.
 */
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
        if (movie.runtime > 0) {
            val formattedRuntime = LocalContext.current.resources.getQuantityString(
                R.plurals.movie_runtime_in_minutes,
                movie.runtime,
                movie.runtime
            )
            append("⏱️ $textRuntime: $formattedRuntime\n")
        }
        if (movie.genres.isNotEmpty()) {
            append("🎭 $textGenres: ${movie.genres.joinToString { it.name }}\n")
        }
        append("\n📖 ${movie.overview}")
        append("\n\n$textTags #${movie.title.replace(" ", "")}")
    }
}