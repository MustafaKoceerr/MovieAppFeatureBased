package com.mustafakocer.feature_movies.home.domain.usecase

import com.mustafakocer.core_domain.util.Resource
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.feature_movies.home.domain.repository.HomeRepository
import com.mustafakocer.feature_movies.shared.domain.model.MovieCategory
import com.mustafakocer.feature_movies.shared.domain.model.MovieListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * An orchestrator use case responsible for fetching all data required for the home screen.
 *
 * Its primary responsibilities are:
 * 1. Reactively listen for changes in the application's language setting.
 * 2. Upon language change or initial call, trigger a parallel fetch for all movie categories.
 * 3. Combine the results from all parallel fetches into a single, unified `Resource` stream.
 *
 * Architectural Decision: This use case centralizes the business logic (the "policy") for the home
 * screen's data aggregation. By abstracting this complexity away from the ViewModel, we achieve a
 * cleaner separation of concerns, making the ViewModel leaner and the business logic more reusable
 * and testable.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GetHomeScreenDataUseCase @Inject constructor(
    private val homeRepository: HomeRepository,
    private val languageRepository: LanguageRepository,
) {
    /**
     * Invokes the use case.
     *
     * @param isRefresh When true, forces a data refresh, bypassing any cached data.
     * @return A [Flow] that emits [Resource] states, containing a map of movie categories to their
     * respective movie lists.
     */
    operator fun invoke(isRefresh: Boolean = false): Flow<Resource<Map<MovieCategory, List<MovieListItem>>>> {
        // Architectural Decision: `flatMapLatest` is crucial for reactivity. It subscribes to the
        // `languageFlow`. Whenever the language changes, it automatically cancels the previous
        // data fetching operation (the `fetchAllCategories` flow) and initiates a new one. This
        // ensures the UI always displays data consistent with the currently selected language
        // without requiring manual refresh triggers from the ViewModel.
        return languageRepository.languageFlow.flatMapLatest {
            fetchAllCategories(isRefresh)
        }
    }

    private fun fetchAllCategories(isRefresh: Boolean): Flow<Resource<Map<MovieCategory, List<MovieListItem>>>> {
        val categories = MovieCategory.getAllCategories()

        val categoryFlows: List<Flow<Resource<List<MovieListItem>>>> = categories.map { category ->
            homeRepository.getMoviesForCategory(category, isRefresh)
        }

        // Architectural Decision: The `combine` operator executes all category data fetches in parallel.
        // It collects the most recent emission from each of the `categoryFlows`. This is far more
        // efficient than fetching categories sequentially, dramatically improving screen load time.
        return combine(categoryFlows) { resources ->
            val resultMap = mutableMapOf<MovieCategory, List<MovieListItem>>()
            var firstError: Resource.Error? = null

            resources.forEachIndexed { index, resource ->
                when (resource) {
                    is Resource.Success -> {
                        // Only add successful results with non-empty data to the map.
                        if (resource.data.isNotEmpty()) {
                            resultMap[categories[index]] = resource.data
                        }
                    }

                    is Resource.Error -> {
                        // Error Handling Strategy: To create a resilient UI, we prioritize showing
                        // partial data. We only capture the first error encountered. This error will
                        // only be propagated to the UI if *all* network requests fail.
                        if (firstError == null) {
                            firstError = resource
                        }
                    }

                    is Resource.Loading -> {
                    }
                }
            }

            // Determine the final result to emit.
            val result: Resource<Map<MovieCategory, List<MovieListItem>>> =
                if (resultMap.isNotEmpty()) {
                    // If we have any data, consider it a success, even if other calls failed.
                    Resource.Success(resultMap)
                } else // If we have no data at all and an error occurred, propagate the error.
                    firstError
                        ?: // If there's no data and no error (e.g., all lists were empty), return success with an empty map.
                        Resource.Success(emptyMap())
            result
        }.onStart {
            // Emit a `Loading` state immediately when the flow execution begins. This allows the
            // UI to show a loading indicator as soon as the data fetch is initiated.
            emit(Resource.Loading)
        }
    }
}