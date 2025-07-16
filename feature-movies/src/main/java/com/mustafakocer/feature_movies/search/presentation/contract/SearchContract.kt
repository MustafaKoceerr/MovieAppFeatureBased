package com.mustafakocer.feature_movies.search.presentation.contract

import androidx.paging.PagingData
import com.mustafakocer.core_common.exception.AppException
import com.mustafakocer.core_common.presentation.BaseUiEffect
import com.mustafakocer.core_common.presentation.BaseUiEvent
import com.mustafakocer.core_common.presentation.BaseUiState
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow


// ==================== STATE ====================
data class SearchUiState(
    // Kullanıcının arama kutusuna girdiği anlık metin.
    val searchQuery: String = "",
    // Paging 3'ten gelen arama sonuçları akışı.
    val searchResults: Flow<PagingData<MovieListItem>> = emptyFlow(),

    // isLoading, debounce sonrası arama başladığında kullanılabilir.
    override val isLoading: Boolean = false,
    override val isRefreshing: Boolean = false, // Paging'in kendi refresh'i var ama tutarlılık için kalabilir.
    override val error: AppException? = null,
) : BaseUiState {

    // Arama yapmak için yeterli karakter girilip girilmediğini kontrol eder.
    val canSearch: Boolean
        get() = searchQuery.trim().length >= 3

    // göstermek için kullanılabilir.
    val showInitialPrompt: Boolean
        get() = searchQuery.isEmpty()
}

// ==================== EVENT ====================
sealed interface SearchEvent : BaseUiEvent {
    data class QueryChanged(val query: String) : SearchEvent
    object ClearSearch : SearchEvent
    data class MovieClicked(val movieId: Int) : SearchEvent
    object BackClicked : SearchEvent

}

// ==================== EFFECT ====================
sealed interface SearchEffect : BaseUiEffect {
    data class NavigateToMovieDetail(val movieId: Int) : SearchEffect
    object NavigateBack : SearchEffect
    data class ShowSnackbar(val message: String) : SearchEffect
    object HideKeyboard : SearchEffect // Arama yapıldığında klavyeyi gizlemek için kullanışlı.

}
