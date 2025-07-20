package com.mustafakocer.movieappfeaturebasedclean.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.core_database.pagination.RemoteKey
import com.mustafakocer.feature_movies.home.data.local.dao.HomeMovieDao
import com.mustafakocer.feature_movies.shared.data.local.converter.MovieConverters
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.shared.data.local.entity.HomeMovieEntity
import com.mustafakocer.feature_movies.shared.data.local.entity.MovieListEntity

/**
 * Uygulamanın nihai ve tek veritabanı sınıfı.
 * Bu sınıf, :di modülünde bulunur çünkü tüm feature modüllerine erişimi olan tek yer burasıdır.
 * Tüm Entity'leri ve DAO'ları burada listeleriz.
 */
internal object DatabaseConstants {
    const val DATABASE_NAME = "movie_app_database.db"
    const val DATABASE_VERSION = 2
}

@Database(
    entities = [
        // Core Entities
        RemoteKey::class,

        // Feature:Movies Entities
        MovieListEntity::class,
        HomeMovieEntity::class,

        // Gelecekteki diğer feature'ların entity'leri buraya eklenecek...
    ],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = false // Portfolyo projesi için false kalabilir, productta değiştirmeyi unutma
)
@TypeConverters(MovieConverters::class) // 🎯 Converter'ları ekle
abstract class AppDatabase : RoomDatabase() {

    // Gerekli tüm DAO'ları burada abstract fonksiyon olarak tanımla
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun movieListDao(): MovieListDao
    abstract fun homeMovieDao(): HomeMovieDao

// Gelecekteki diğer feature'ların DAO'ları buraya eklenecek...
}
