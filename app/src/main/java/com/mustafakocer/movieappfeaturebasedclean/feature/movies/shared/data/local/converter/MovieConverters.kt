package com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.local.converter

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/**
 * Room `TypeConverter`s for types it cannot store natively (here: `List<Int>`, e.g. genre ids).
 *
 * The list is stored as a JSON string (`[1,2,3]`) in a TEXT column, using kotlinx-serialization
 * like the rest of the project.
 */
class MovieConverters {

    private val intListSerializer = ListSerializer(Int.serializer())

    @TypeConverter
    fun fromIntList(value: List<Int>?): String? =
        value?.let { Json.encodeToString(intListSerializer, it) }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? =
        value?.let {
            try {
                Json.decodeFromString(intListSerializer, it)
            } catch (e: Exception) {
                emptyList()
            }
        }
}
