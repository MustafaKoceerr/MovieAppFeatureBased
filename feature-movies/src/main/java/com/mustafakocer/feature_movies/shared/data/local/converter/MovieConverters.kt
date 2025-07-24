package com.mustafakocer.feature_movies.shared.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Provides `TypeConverter`s for the Room database to handle data types that it cannot
 * store natively, such as lists of primitives.
 *
 * Architectural Decision: Room can only store primitive data types (like String, Int, etc.) in its
 * columns. To persist more complex types like a `List<Int>`, we must provide a way to convert it
 * to and from a supported type. This class uses JSON serialization (via Gson) to convert the list
 * into a simple `String` for storage in a TEXT column and back again upon retrieval. This approach
 * centralizes the data conversion logic, keeping the Entity definitions clean.
 */
class MovieConverters {

    private val gson = Gson()

    /**
     * Converts a list of integers into a JSON string for database storage.
     *
     * @param value The list of integers to convert (e.g., a list of genre IDs).
     * @return A JSON string representation of the list, or null if the input list is null.
     */
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let { gson.toJson(it) }
    }

    /**
     * Converts a JSON string from the database back into a list of integers.
     *
     * @param value The JSON string retrieved from the database.
     * @return The deserialized list of integers, or null if the input string is null.
     */
    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.let {
            try {
                val listType = object : TypeToken<List<Int>>() {}.type
                gson.fromJson<List<Int>>(it, listType)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}