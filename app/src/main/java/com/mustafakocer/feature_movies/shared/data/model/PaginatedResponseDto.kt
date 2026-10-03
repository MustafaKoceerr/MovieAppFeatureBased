package com.mustafakocer.feature_movies.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A generic Data Transfer Object (DTO) designed to represent the standard structure of a
 * paginated response from an API like TMDB.
 *
 * Architectural Decision: This class is a crucial part of the data layer. By creating a model
 * that directly mirrors the external API's JSON structure, we isolate the rest of the application
 * from the specifics of the network schema. The use of a generic type `<T>` makes this DTO highly
 * reusable for any kind of paginated data (e.g., movies, TV shows, actors), reducing code
 * duplication. Nullable properties are used to make the deserialization process robust against
 * optional or missing fields in the API response.
 *
 * @param T The type of the items contained in the results list.
 * @property page The current page number of the paginated response.
 * @property results The list of data items for the current page. Can be null or empty.
 * @property totalPages The total number of pages available.
 * @property totalResults The total number of items available across all pages.
 * @property dates An optional object containing date range information, typically only present
 *                 on specific endpoints like "now_playing" or "upcoming".
 */
@Serializable
data class PaginatedResponseDto<T>(
    @SerialName("page")
    val page: Int,

    @SerialName("results")
    val results: List<T>?,

    @SerialName("total_pages")
    val totalPages: Int,

    @SerialName("total_results")
    val totalResults: Int,

    @SerialName("dates")
    val dates: DatesDto? = null,
)

/**
 * A DTO representing the date range for movie releases, used within [PaginatedResponseDto].
 *
 * @property maximum The latest date in the range.
 * @property minimum The earliest date in the range.
 */
@Serializable
data class DatesDto(
    @SerialName("maximum")
    val maximum: String?,

    @SerialName("minimum")
    val minimum: String?,
)