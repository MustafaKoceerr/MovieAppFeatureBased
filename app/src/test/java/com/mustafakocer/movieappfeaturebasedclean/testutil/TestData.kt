package com.mustafakocer.movieappfeaturebasedclean.testutil

import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieDetails
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem

fun movieListItem(id: Int = 1, title: String = "Movie $id") = MovieListItem(
    id = id,
    title = title,
    overview = "Overview of $title",
    posterUrl = null,
    releaseYear = "2024",
    voteAverage = 7.5,
    voteCount = 100,
)

fun movieDetails(id: Int = 1, title: String = "Movie $id") = MovieDetails(
    id = id,
    title = title,
    overview = "Overview of $title",
    posterUrl = "",
    backdropUrl = "",
    releaseDate = "2024-01-01",
    voteAverage = 7.5,
    runtime = 120,
    tagline = "",
    genres = emptyList(),
)
