package com.mustafakocer.feature_movies.details.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseViewModel
import com.mustafakocer.core_common.presentation.LoadingType
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
            is MovieDetailsEvent.ShareMovie -> handleShareMovie()
            is MovieDetailsEvent.BackPressed -> sendEffect(MovieDetailsEffect.NavigateBack) // bu fonksiyon baseViewModel'den geliyor
            is MovieDetailsEvent.DismissError -> setState { copy(error = null) } // bu fonksiyon baseViewModel'den geliyor.

        }
    }

    private fun loadMovieDetails(loadingType: LoadingType) {
        // Karmaşık collect bloğu yerine, BaseViewModel'deki safeLaunch'ı kullanıyoruz.
        executeSafeOnce(loadingType) {
            val details = getMovieDetailsUseCase(movieId).first()
            setState { copy(movie = details) }
        }
    }

    private fun handleShareMovie() {
        val movie = currentState.movie ?: return // Film detayı yoksa paylaşma

        setState { copy(isSharing = true) }

        val shareTitle = "Check out this movie!"
        val shareContent = buildShareContent(movie)
        sendEffect(MovieDetailsEffect.ShareContent(shareTitle, shareContent))

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
            LoadingType.MAIN -> currentState.copy(isLoading = isLoading)
            LoadingType.REFRESH -> currentState.copy(isRefreshing = isLoading)
        }
    }

    /**
     * Build formatted content for sharing
     */
    private fun buildShareContent(movie: MovieDetails): String {
        return buildString {
            append("🎬 ${movie.title}\n\n")

            if (movie.hasTagline) {
                append("\"${movie.tagline}\"\n\n")
            }

            append("⭐ Rating: ${movie.voteAverage.formattedRating}/10\n")
            append("📅 Release: ${movie.releaseDate}\n")

            movie.runtime?.let {
                append("⏱️ Runtime: $it\n")

            }

            if (movie.genres.isNotEmpty()) {
                append("🎭 Genres: ${movie.genres.joinToString { it.name }}\n")
            }

            append("\n📖 ${movie.overview}")
            append("\n\n#Movies #Cinema #${movie.title.replace(" ", "")}")
        }
    }

}