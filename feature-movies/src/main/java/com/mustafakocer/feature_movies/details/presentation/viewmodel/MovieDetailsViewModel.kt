package com.mustafakocer.feature_movies.details.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.mustafakocer.core_android.presentation.BaseViewModel
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_android.presentation.LoadingType
import com.mustafakocer.feature_movies.details.domain.usecase.GetMovieDetailsUseCase
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEffect
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsEvent
import com.mustafakocer.feature_movies.details.presentation.contract.MovieDetailsUiState
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import com.mustafakocer.feature_movies.shared.util.formattedRating
import com.mustafakocer.navigation_contracts.navigation.MovieDetailsScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<MovieDetailsUiState, MovieDetailsEvent, MovieDetailsEffect>(
    initialState = MovieDetailsUiState() // Başlangıç state'ini veriyoruz
) {
    private val movieId: Int = savedStateHandle.get<Int>(MovieDetailsScreen.KEY_MOVIE_ID)
        ?: throw IllegalStateException("movieId is required")

    init {
        loadMovieDetails(loadingType = LoadingType.MAIN)
    }

    override fun onEvent(event: MovieDetailsEvent) {
        when (event) {
            is MovieDetailsEvent.Refresh -> loadMovieDetails(loadingType = LoadingType.REFRESH) // todo sonra implement et
            is MovieDetailsEvent.ShareMovie -> handleShareMovie(
                shareTitle = event.shareTitle,
                textRating = event.textRating,
                textRelease = event.textRelease,
                textRuntime = event.textRuntime,
                textGenres = event.textGenres,
                textTags = event.textTags
            )

            is MovieDetailsEvent.BackPressed -> sendEffect(
                MovieDetailsEffect.NavigateBack
            ) // bu fonksiyon baseViewModel'den geliyor
            is MovieDetailsEvent.DismissError -> setState {
                copy(
                    error = null
                )
            } // bu fonksiyon baseViewModel'den geliyor.

        }
    }

    private fun loadMovieDetails(loadingType: LoadingType) {
        // Karmaşık collect bloğu yerine, BaseViewModel'deki safeLaunch'ı kullanıyoruz.
        executeSafeOnce(loadingType) {
            val details = getMovieDetailsUseCase(movieId).first()
            setState { copy(movie = details) }
        }
    }

    private fun handleShareMovie(
        shareTitle: String,
        textRating: String,
        textRelease: String,
        textRuntime: String,
        textGenres: String,
        textTags: String,
    ) {
        val movie = currentState.movie ?: return // Film detayı yoksa paylaşma

        setState { copy(isSharing = true) }

        val shareContent = buildShareContent(
            movie,
            shareTitle,
            textRating,
            textRelease,
            textRuntime,
            textGenres,
            textTags,
        )
        sendEffect(
            MovieDetailsEffect.ShareContent(
                shareTitle,
                shareContent
            )
        )

        // Paylaşım anlık bir işlem olduğu için, UI'da spinner göstermek adına
        // durumu hemen geri alabiliriz veya bir gecikme ekleyebiliriz.
        // Şimdilik basit tutalım.
        setState { copy(isSharing = false) }
    }


    // BaseViewModel'in zorunlu kıldığı abstract metodları implemente ediyoruz.
    override fun handleError(error: AppException): MovieDetailsUiState {
        return currentState.copy(error = error)
    }

    override fun setLoading(
        loadingType: LoadingType,
        isLoading: Boolean,
    ): MovieDetailsUiState {
        return when (loadingType) {
            LoadingType.MAIN -> currentState.copy(
                isLoading = isLoading
            )

            LoadingType.REFRESH -> currentState.copy(
                isRefreshing = isLoading
            )
        }
    }

    /**
     * Build formatted content for sharing
     */
    private fun buildShareContent(
        movie: MovieDetails,
        shareTitle: String,
        textRating: String,
        textRelease: String,
        textRuntime: String,
        textGenres: String,
        textTags: String,
    ): String {
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

}