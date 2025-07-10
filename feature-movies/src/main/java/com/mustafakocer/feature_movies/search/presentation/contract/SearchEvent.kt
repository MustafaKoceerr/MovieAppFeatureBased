package com.mustafakocer.feature_movies.search.presentation.contract

import com.mustafakocer.core_common.presentation.BaseUiEvent

/**
 * Search UI Events - User Actions
 *
 * RESPONSIBILITY: Define user interactions with search screen
 */
sealed interface SearchEvent : BaseUiEvent {

    /**
     * User typed in search input
     */
    data class QueryChanged(val query: String) : SearchEvent

    /**
     * User pressed search button or enter
     */
    object SearchTriggered : SearchEvent

    /**
     * User clicked clear search button
     */
    object ClearSearch : SearchEvent

}
