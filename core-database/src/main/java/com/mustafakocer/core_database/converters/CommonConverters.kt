package com.mustafakocer.core_database.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement


/**
 * Evet, bu fonksiyonları generic olarak yazmak teorik olarak mümkün ama Kotlin’de reified generic types yalnızca inline fonksiyonlarda çalıştığı için doğrudan @TypeConverter olarak kullanılamaz.
 *
 * 📌 Sorunun Özeti:
 * Senin örneğinde:
 *
 * kotlin
 * Copy
 * Edit
 * @TypeConverter
 * fun <T> toList(value: String): List<T> {
 *     return json.decodeFromString(value) // HATA!
 * }
 * Bu hata verir çünkü decodeFromString'in generic tipini (List<T>) runtime'da yansıma (reified) olmadan bilemez. Yani, List<T> tipi deserialize edilemez çünkü Kotlin'de tip silinir (type erasure).
 */

/**
 * Common Room type converters for complex data types
 *
 * CLEAN ARCHITECTURE: Infrastructure Layer - Data Conversion
 * RESPONSIBILITY: Convert complex types to/from database-compatible types
 *
 * DESIGN PATTERN: Converter Pattern
 * - Bidirectional type conversion
 * - JSON serialization for complex types
 * - Reusable across all entities
 */
class CommonConverters {

    /**
     * Public JSON instance for shared use across all converters
     * Public visibility is required for inline functions to access this instance
     */
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    // ==================== LIST CONVERTERS ====================

    /**
     * Convert List<String> to JSON string for database storage
     */
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return json.encodeToString(value)
    }

    /**
     * Convert JSON string to List<String> from database
     */
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Convert List<Int> to JSON string for database storage
     */
    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        return json.encodeToString(value)
    }

    /**
     * Convert JSON string to List<Int> from database
     */
    @TypeConverter
    fun toIntList(value: String): List<Int> {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Convert List<Long> to JSON string for database storage
     */
    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return json.encodeToString(value)
    }

    /**
     * Convert JSON string to List<Long> from database
     */
    @TypeConverter
    fun toLongList(value: String): List<Long> {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Convert List<Double> to JSON string for database storage
     */
    @TypeConverter
    fun fromDoubleList(value: List<Double>): String {
        return json.encodeToString(value)
    }

    /**
     * Convert JSON string to List<Double> from database
     */
    @TypeConverter
    fun toDoubleList(value: String): List<Double> {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ==================== MAP CONVERTERS ====================

    /**
     * Convert Map<String, String> to JSON string for database storage
     */
    @TypeConverter
    fun fromStringMap(value: Map<String, String>): String {
        return json.encodeToString(value)
    }

    /**
     * Convert JSON string to Map<String, String> from database
     */
    @TypeConverter
    fun toStringMap(value: String): Map<String, String> {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    /**
     * Convert Map<String, Int> to JSON string for database storage
     */
    @TypeConverter
    fun fromStringIntMap(value: Map<String, Int>): String {
        return json.encodeToString(value)
    }

    /**
     * Convert JSON string to Map<String, Int> from database
     */
    @TypeConverter
    fun toStringIntMap(value: String): Map<String, Int> {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    /**
     * Convert Map<String, Any> to JSON string for database storage
     * Note: Use with caution, prefer specific types when possible
     */
    @TypeConverter
    fun fromStringAnyMap(value: Map<String, @JvmSuppressWildcards Any>): String {
        return json.encodeToString(value)
    }

    /**
     * Convert JSON string to Map<String, Any> from database
     * Note: This returns Map<String, JsonElement>, you'll need to cast appropriately
     */
    @TypeConverter
    fun toStringAnyMap(value: String): Map<String, @JvmSuppressWildcards Any> {
        return try {
            json.decodeFromString<Map<String, JsonElement>>(value)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    // ==================== SET CONVERTERS ====================

    /**
     * Convert Set<String> to JSON string for database storage
     */
    @TypeConverter
    fun fromStringSet(value: Set<String>): String {
        return json.encodeToString(value.toList())
    }

    /**
     * Convert JSON string to Set<String> from database
     */
    @TypeConverter
    fun toStringSet(value: String): Set<String> {
        return try {
            json.decodeFromString<List<String>>(value).toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    /**
     * Convert Set<Int> to JSON string for database storage
     */
    @TypeConverter
    fun fromIntSet(value: Set<Int>): String {
        return json.encodeToString(value.toList())
    }

    /**
     * Convert JSON string to Set<Int> from database
     */
    @TypeConverter
    fun toIntSet(value: String): Set<Int> {
        return try {
            json.decodeFromString<List<Int>>(value).toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    // ==================== ENUM CONVERTERS ====================

    /**
     * Generic enum converter - convert enum to string
     * Usage: Store enum as string in database
     */
    inline fun <reified T : Enum<T>> fromEnum(value: T): String {
        return value.name
    }

    /**
     * Generic enum converter - convert string to enum
     * Usage: Restore enum from database string
     */
    inline fun <reified T : Enum<T>> toEnum(value: String): T? {
        return try {
            enumValueOf<T>(value)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    // ==================== NULLABLE CONVERTERS ====================

    /**
     * Convert nullable List<String> to JSON string for database storage
     */
    @TypeConverter
    fun fromNullableStringList(value: List<String>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    /**
     * Convert JSON string to nullable List<String> from database
     */
    @TypeConverter
    fun toNullableStringList(value: String?): List<String>? {
        return value?.let {
            try {
                json.decodeFromString(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Convert nullable Map<String, String> to JSON string for database storage
     */
    @TypeConverter
    fun fromNullableStringMap(value: Map<String, String>?): String? {
        return value?.let { json.encodeToString(it) }
    }

    /**
     * Convert JSON string to nullable Map<String, String> from database
     */
    @TypeConverter
    fun toNullableStringMap(value: String?): Map<String, String>? {
        return value?.let {
            try {
                json.decodeFromString(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    // ==================== CUSTOM OBJECT CONVERTERS ====================

    /**
     * Generic object converter - convert any serializable object to JSON
     * Note: T must be annotated with @Serializable
     * Uses shared JSON instance for performance
     */
    inline fun <reified T> fromObject(value: T): String {
        return json.encodeToString(value)
    }

    /**
     * Generic object converter - convert JSON to any serializable object
     * Note: T must be annotated with @Serializable
     * Uses shared JSON instance for performance
     */
    inline fun <reified T> toObject(value: String): T? {
        return try {
            json.decodeFromString<T>(value)
        } catch (e: Exception) {
            null
        }
    }

    // ==================== SAFE CONVERTERS ====================

    /**
     * Safe JSON encoding with fallback
     * Returns fallback value if encoding fails
     */
    inline fun <reified T> safeEncode(value: T, fallback: String = "{}"): String {
        return try {
            json.encodeToString(value)
        } catch (e: Exception) {
            fallback
        }
    }

    /**
     * Safe JSON decoding with fallback
     * Returns fallback value if decoding fails
     */
    inline fun <reified T> safeDecode(value: String, fallback: T): T {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            fallback
        }
    }

    /**
     * Safe list converter - convert JSON string to list with fallback
     * Usage: safeListFromJson<Movie>(jsonString, emptyList())
     */
    inline fun <reified T> safeListFromJson(
        value: String,
        fallback: List<T> = emptyList(),
    ): List<T> {
        return try {
            json.decodeFromString<List<T>>(value)
        } catch (e: Exception) {
            fallback
        }
    }

    /**
     * Safe map converter - convert JSON string to map with fallback
     * Usage: safeMapFromJson<String, Int>(jsonString, emptyMap())
     */
    inline fun <reified K, reified V> safeMapFromJson(
        value: String,
        fallback: Map<K, V> = emptyMap(),
    ): Map<K, V> {
        return try {
            json.decodeFromString<Map<K, V>>(value)
        } catch (e: Exception) {
            fallback
        }
    }

    // ==================== UTILITY FUNCTIONS ====================

    /**
     * Check if string is valid JSON
     */
    fun isValidJson(value: String): Boolean {
        return try {
            json.parseToJsonElement(value)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Compact JSON string (remove unnecessary whitespace)
     */
    fun compactJson(value: String): String {
        return try {
            val element = json.parseToJsonElement(value)
            json.encodeToString(element)
        } catch (e: Exception) {
            value
        }
    }

    /**
     * Pretty print JSON string with indentation
     */
    fun prettyJson(value: String): String {
        val prettyJson = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
            prettyPrint = true
        }
        return try {
            val element = json.parseToJsonElement(value)
            prettyJson.encodeToString(element)
        } catch (e: Exception) {
            value
        }
    }

    /**
     * Get JSON string size in bytes
     */
    fun getJsonSize(value: String): Int {
        return value.toByteArray(Charsets.UTF_8).size
    }

    /**
     * Validate and sanitize JSON string
     * Returns sanitized JSON or empty object if invalid
     */
    fun sanitizeJson(value: String): String {
        return if (isValidJson(value)) {
            compactJson(value)
        } else {
            "{}"
        }
    }
}