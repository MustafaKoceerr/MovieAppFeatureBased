package com.mustafakocer.core_database.converters

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// ==================== EXTENSION FUNCTIONS ====================

/**
 * Convert any list to JSON string using shared converter
 */
fun <T> List<T>.toJsonString(): String {
    val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    return json.encodeToString(this)
}

/**
 * Convert JSON string to list of specific type
 */
inline fun <reified T> String.toList(): List<T> {
    val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    return try {
        json.decodeFromString(this)
    } catch (e: Exception) {
        emptyList()
    }
}

/**
 * Convert any map to JSON string using shared converter
 */
fun <K, V> Map<K, V>.toJsonString(): String {
    val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    return json.encodeToString(this)
}

/**
 * Convert JSON string to map of specific types
 */
inline fun <reified K, reified V> String.toMap(): Map<K, V> {
    val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    return try {
        json.decodeFromString(this)
    } catch (e: Exception) {
        emptyMap()
    }
}

/**
 * Safe JSON string to object conversion with fallback
 */
inline fun <reified T> String.toObjectSafe(fallback: T): T {
    val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    return try {
        json.decodeFromString(this)
    } catch (e: Exception) {
        fallback
    }
}