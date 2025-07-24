package com.mustafakocer.feature_movies.details.domain.usecase

import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.feature_movies.details.domain.repository.MovieDetailsRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

/**
 * A use case that provides the details for a specific movie, reactively updating the
 * data whenever the application's language changes.
 *
 * @param repository The repository for fetching movie details.
 * @param languageRepository The repository for observing language preference changes.
 *
 * Architectural Note:
 * This use case demonstrates a powerful reactive pattern. It combines two streams of data:
 * the movie details and the current language preference.
 * - It uses `flatMapLatest` to listen to the `languageFlow`.
 * - When the language changes, `flatMapLatest` automatically cancels the previous data request
 *   and triggers a new call to `repository.getMovieDetails`.
 * This ensures the UI always displays data in the correct language without needing manual
 * refresh logic in the ViewModel. The use case itself doesn't pass the language code; its
 * role is simply to react to the change event. The actual language parameter is appended to
 * network requests by a lower-level interceptor.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieDetailsRepository,
    private val languageRepository: LanguageRepository,
) {
    /**
     * @param movieId The ID of the movie for which to fetch details.
     * @return A [Flow] of [Resource]<[MovieDetails]> that is sensitive to language changes.
     */
    operator fun invoke(movieId: Int): Flow<Resource<MovieDetails>> {
        require(movieId > 0) { "Movie ID must be positive, but was: $movieId" }

        return languageRepository.languageFlow
            .flatMapLatest {
                repository.getMovieDetails(movieId)
            }
    }
}