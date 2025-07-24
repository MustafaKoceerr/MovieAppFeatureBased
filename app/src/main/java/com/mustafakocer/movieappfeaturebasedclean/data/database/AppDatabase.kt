package com.mustafakocer.movieappfeaturebasedclean.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.core_database.pagination.RemoteKey
import com.mustafakocer.feature_movies.home.data.local.dao.HomeMovieDao
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.shared.data.local.converter.MovieConverters
import com.mustafakocer.feature_movies.shared.data.local.entity.HomeMovieEntity
import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity

/**
 * Centralizes database metadata, such as the name and version, for easy management.
 */
internal object DatabaseConstants {
    const val DATABASE_NAME = "movie_app_database.db"
    const val DATABASE_VERSION = 2
}

/**
 * The definitive and sole Room database class for the entire application.
 *
 * Architectural Decision: This `AppDatabase` class resides in the main `:app` module because it
 * is the only module that has visibility over all other feature and core modules. This central
 * location allows it to declare all the necessary `Entities` and `DAOs` from across the entire
 * application in a single, unified database definition. This approach is fundamental to creating
 * a single, cohesive database for a multi-module application.
 *
 * @property entities An array of all data classes that should be mapped to tables in the database.
 * @property version The version of the database schema. This must be incremented when the schema
 *                   changes to trigger Room's migration process.
 * @property exportSchema When set to `true`, Room exports the database schema into a JSON file in
 *                        the project directory. This is highly recommended for production apps to
 *                        track schema history and write robust migrations. For a portfolio project,
 *                        `false` is acceptable for simplicity.
 */
@Database(
    entities = [
        // Core Entities (e.g., for pagination)
        RemoteKey::class,

        // Feature:Movies Entities
        MovieListEntity::class,
        HomeMovieEntity::class,

        // Entities from other future features would be added here...
    ],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(MovieConverters::class)
abstract class AppDatabase : RoomDatabase() {

    // Architectural Decision: For each DAO interface, an abstract function is declared here.
    // At compile time, Room's annotation processor will generate the concrete implementation
    // of these functions, providing the application with ready-to-use DAO instances.

    abstract fun remoteKeyDao(): RemoteKeyDao

    abstract fun movieListDao(): MovieListDao

    abstract fun homeMovieDao(): HomeMovieDao

    // DAOs from other future features would be added here...
}