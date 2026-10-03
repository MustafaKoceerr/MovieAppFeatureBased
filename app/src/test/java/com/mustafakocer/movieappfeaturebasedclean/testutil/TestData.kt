package com.mustafakocer.movieappfeaturebasedclean.testutil

import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.model.MovieDetailsDto
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.model.MovieDto
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.model.PaginatedResponseDto
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieDetails
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieListItem
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

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

fun movieDto(
    id: Int = 1,
    title: String? = "Movie $id",
    overview: String? = "Overview $id",
    releaseDate: String? = "2024-05-01",
) = MovieDto(
    id = id,
    title = title,
    overview = overview,
    posterPath = "/poster$id.jpg",
    backdropPath = null,
    releaseDate = releaseDate,
    voteAverage = 7.5,
    voteCount = 100,
    genreIds = listOf(1, 2),
    adult = false,
    popularity = 1.0,
    originalLanguage = "en",
    originalTitle = title,
    video = false,
)

fun movieDetailsDto(id: Long = 1, title: String? = "Movie $id") = MovieDetailsDto(id = id, title = title)

fun <T> paginatedResponse(
    results: List<T>?,
    page: Int = 1,
    totalPages: Int = 1,
) = PaginatedResponseDto(
    page = page,
    results = results,
    totalPages = totalPages,
    totalResults = results?.size ?: 0,
)

fun <T> httpError(code: Int): Response<T> = Response.error(code, "".toResponseBody(null))
