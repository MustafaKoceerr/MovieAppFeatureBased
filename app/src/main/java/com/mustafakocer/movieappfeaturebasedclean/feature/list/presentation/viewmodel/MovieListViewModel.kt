package com.mustafakocer.movieappfeaturebasedclean.feature.list.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mustafakocer.movieappfeaturebasedclean.feature.list.data.repository.MovieListRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieCategory
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import com.mustafakocer.movieappfeaturebasedclean.navigation.MovieListScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Paging drives the whole screen, so there is no UiState: the category comes from the route
 * argument and the paged movies are exposed as a separate flow (never stored inside a state class).
 */
@HiltViewModel
class MovieListViewModel @Inject constructor(
    movieListRepository: MovieListRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /** Null when the route carries an unknown endpoint; the route then navigates back. */
    val category: MovieCategory? =
        MovieCategory.fromApiEndpoint(savedStateHandle.toRoute<MovieListScreen>().categoryEndpoint)

    val movies: Flow<PagingData<MovieListItem>> =
        category?.let { movieListRepository.getMoviesByCategory(it).cachedIn(viewModelScope) } ?: emptyFlow()
}
