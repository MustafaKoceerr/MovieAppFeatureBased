package com.mustafakocer.feature_movies.list.domain.usecase

import androidx.paging.PagingData
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.feature_movies.list.domain.repository.MovieListRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * A use case that provides a reactive, paginated stream of movies for a given category.
 *
 * This use case encapsulates the business logic of fetching a movie list while automatically
 * reacting to changes in the application's selected language.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetMovieListUseCase @Inject constructor(
    private val repository: MovieListRepository,
    private val languageRepository: LanguageRepository,
) {
    /**
     * Invokes the use case to get a language-aware flow of paginated movie data.
     *
     * @param category The [MovieCategory] for which to retrieve the movie list.
     * @return A [Flow] of [PagingData] that will automatically update and fetch new data
     *         whenever the application's language setting changes.
     */
    operator fun invoke(category: MovieCategory): Flow<PagingData<MovieListItem>> {
        // Architectural Decision: The use case subscribes to the `languageFlow` from the
        // LanguageRepository. `flatMapLatest` is the key operator here:
        // 1. Whenever the language changes, `flatMapLatest` cancels the previous Flow (the
        //    pager for the old language) and creates a new one by calling the repository
        //    with the new language parameter.
        // 2. This makes the entire data pipeline reactive and self-correcting. The ViewModel
        //    doesn't need to be aware of language changes; it simply collects this flow,
        //    and the data will automatically be refreshed with the correct language content.
        return languageRepository.languageFlow
            .flatMapLatest { language ->
                repository.getMoviesByCategory(
                    category = category,
                    language = language.apiParam
                )
            }
    }
}