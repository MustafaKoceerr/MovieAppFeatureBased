package com.mustafakocer.feature_movies.home.domain.usecase

import com.mustafakocer.core_common.result.UiState
import com.mustafakocer.feature_movies.home.domain.model.Movie
import com.mustafakocer.feature_movies.home.domain.model.MovieCategoryType
import com.mustafakocer.feature_movies.home.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * TEACHING MOMENT: Individual Category Use Case
 *
 * ✅ BENEFITS:
 * - Load one category at a time
 * - Independent error handling
 * - Selective retry capability
 * - Better user experience
 */
class GetMovieCategoryUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    operator fun invoke(
        categoryType: MovieCategoryType,
        page: Int = 1,
    ): Flow<UiState<List<Movie>>> {
        return when (categoryType) {
            MovieCategoryType.NOW_PLAYING -> movieRepository.getNowPlayingMovies(page)
            MovieCategoryType.POPULAR -> movieRepository.getPopularMovies(page)
            MovieCategoryType.TOP_RATED -> movieRepository.getTopRatedMovies(page)
            MovieCategoryType.UPCOMING -> movieRepository.getUpcomingMovies(page)
        }
    }

}