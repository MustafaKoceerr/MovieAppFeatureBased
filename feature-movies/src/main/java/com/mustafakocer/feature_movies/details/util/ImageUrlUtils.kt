package com.mustafakocer.feature_movies.details.util

import com.mustafakocer.feature_movies.shared.util.MovieConstants
import com.mustafakocer.feature_movies.details.domain.model.MovieDetails

fun MovieDetails.fullPosterUrl(): String {
    return if (!posterPath.isNullOrBlank()) {
        "${MovieConstants.IMAGE_BASE_URL}${MovieConstants.POSTER_SIZE}$posterPath"
    } else {
        ""
    }
}

fun MovieDetails.fullBackdropUrl(): String {
    return if (!backdropPath.isNullOrBlank()) {
        "${MovieConstants.IMAGE_BASE_URL}${MovieConstants.BACKDROP_SIZE}$backdropPath"
    } else {
        ""
    }
}