package com.mustafakocer.feature_movies.home.domain.usecase

import com.mustafakocer.core_common.util.Resource
import com.mustafakocer.feature_movies.home.domain.repository.MovieRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieCategoryUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    operator fun invoke(
        categoryType: MovieCategory,
        page: Int = 1,
    ): Flow<Resource<List<MovieListItem>>> {
        return when (categoryType) {
            MovieCategory.NOW_PLAYING -> movieRepository.getNowPlayingMovies(page)
            MovieCategory.POPULAR -> movieRepository.getPopularMovies(page)
            MovieCategory.TOP_RATED -> movieRepository.getTopRatedMovies(page)
            MovieCategory.UPCOMING -> movieRepository.getUpcomingMovies(page)
        }
    }

}