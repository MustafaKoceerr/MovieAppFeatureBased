package com.mustafakocer.feature_movies.shared.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/**
 * Room TypeConverters for automatic data type conversion
 */
class MovieConverters {

    private val gson = Gson()

    // List<Int> converters (for genreIds, etc.)
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let { gson.toJson(it) }
    }

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