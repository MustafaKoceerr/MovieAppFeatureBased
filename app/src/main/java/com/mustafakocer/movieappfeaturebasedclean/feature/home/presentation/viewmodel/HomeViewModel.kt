package com.mustafakocer.movieappfeaturebasedclean.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mustafakocer.core_domain.exception.AppException
import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.movieappfeaturebasedclean.feature.home.domain.usecase.GetHomeScreenDataUseCase
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieCategory
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val categories: Map<MovieCategory, List<MovieListItem>> = emptyMap(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: AppException? = null,
) {
    /** Full-screen skeleton only while there is nothing to show yet. */
    val showFullScreenLoading: Boolean
        get() = isLoading && categories.isEmpty()

    /** Full-screen error only if there is no (cached) content to fall back on. */
    val showFullScreenError: Boolean
        get() = error != null && categories.isEmpty()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenDataUseCase: GetHomeScreenDataUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var dataCollectionJob: Job? = null

    init {
        loadData(isRefresh = false)
    }

    fun onRefresh() = loadData(isRefresh = true)

    private fun loadData(isRefresh: Boolean) {
        if (isRefresh) _uiState.update { it.copy(isRefreshing = true) }

        dataCollectionJob?.cancel()
        dataCollectionJob = getHomeScreenDataUseCase(isRefresh = isRefresh)
            .onEach { resource ->
                when (resource) {
                    is Resource.Loading -> _uiState.update {
                        // Keep showing cached content instead of flashing a skeleton.
                        if (it.categories.isEmpty()) it.copy(isLoading = true, error = null) else it
                    }

                    is Resource.Success -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            categories = resource.data,
                            error = null,
                        )
                    }

                    is Resource.Error -> _uiState.update {
                        it.copy(isLoading = false, isRefreshing = false, error = resource.exception)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}
