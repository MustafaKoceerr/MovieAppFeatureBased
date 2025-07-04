package com.mustafakocer.movieappfeaturebasedclean.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.core_database.pagination.RemoteKey
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListRemoteKeyDao
import com.mustafakocer.feature_movies.list.data.local.entity.MovieListEntity

/**
 * Main application database
 *
 * DATABASE CONTRACTS PATTERN:
 * ✅ Combines entities from all features
 * ✅ Provides DAOs for all features
 * ✅ Centralized database configuration
 * ✅ Dynamic entity registration via registry
 *
 * NOTE: Entity list is built dynamically by AppDatabaseBuilder
 * This annotation is updated at compile time
 *
 *  * NOTE: Room requires compile-time static entity list
 *  * Dynamic registration is handled via Hilt modules
 */
@Database(
    entities = [
        RemoteKey::class,
        MovieListEntity::class,
    ],
    version = 1,
    exportSchema = false // Set to true in production
)
@TypeConverters(
)
abstract class AppDatabase : RoomDatabase() {

    // ==================== CORE DAOS ====================

    abstract fun remoteKeyDao(): RemoteKeyDao
    // ==================== FEATURE DAOS ====================

    // Movies feature DAOs
    abstract fun movieListDao(): MovieListDao
    abstract fun movieListRemoteKeyDao(): MovieListRemoteKeyDao

    // Future feature DAOs
    // abstract fun userDao(): UserDao
    // abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        const val DATABASE_NAME = "movie_app_database"
        const val DATABASE_VERSION = 1
    }
}