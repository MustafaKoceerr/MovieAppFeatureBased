package com.mustafakocer.movieappfeaturebasedclean.feature.search.domain.model

/** A raw user query plus the rules deciding whether it is worth sending to the API. */
data class SearchQuery(val query: String) {

    val cleanQuery: String
        get() = query.trim()

    val isValid: Boolean
        get() = cleanQuery.length in MIN_LENGTH..MAX_LENGTH

    companion object {
        /** Shorter queries are too broad/expensive to search. */
        const val MIN_LENGTH = 3
        const val MAX_LENGTH = 50
    }
}
