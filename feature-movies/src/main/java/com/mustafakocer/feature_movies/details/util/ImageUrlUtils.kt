package com.mustafakocer.feature_movies.details.util

import com.mustafakocer.feature_movies.shared.domain.model.MovieDetails
import com.mustafakocer.feature_movies.shared.util.MovieConstants

fun MovieDetails.fullPosterUrl(): String {
    return if (posterUrl.isNotBlank()) {
        "${MovieConstants.IMAGE_BASE_URL}${MovieConstants.POSTER_SIZE}$posterUrl"
    } else {
        ""
    }
}

fun MovieDetails.fullBackdropUrl(): String {
    return if (backdropUrl.isNotBlank()) {
        "${MovieConstants.IMAGE_BASE_URL}${MovieConstants.BACKDROP_SIZE}$backdropUrl"
    } else {
        ""
    }
}