package com.mustafakocer.feature_movies.shared.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * TMDB gibi sayfalama yapan API'lardan gelen yanıtları karşılamak için
 * tasarlanmış jenerik bir veri transfer nesnesi.
 */
@Serializable
data class PaginatedResponseDto<T>(
    @SerialName("page")
    val page: Int, // Sayfa bilgisi gelmeyebilir

    @SerialName("results")
    val results: List<T>?, // Sonuç listesi boş veya hiç gelmeyebilir

    @SerialName("total_pages")
    val totalPages: Int,

    @SerialName("total_results")
    val totalResults: Int,

    // Sadece "now_playing" ve "upcoming" için gelen, nullable bir alan.
    @SerialName("dates")
    val dates: DatesDto? = null,
)

@Serializable
data class DatesDto(
    @SerialName("maximum")
    val maximum: String?,

    @SerialName("minimum")
    val minimum: String?,
)