package com.mustafakocer.feature_movies.search.domain.usecase

import com.mustafakocer.feature_movies.search.domain.repository.SearchRepository
import javax.inject.Inject

/**
 * Clear search cache use case
 */
class ClearSearchCacheUseCase @Inject constructor(
    private val searchRepository: SearchRepository
){
    suspend operator fun invoke(){
        searchRepository.clearSearchCache()
    }
}