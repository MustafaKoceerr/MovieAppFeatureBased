package com.mustafakocer.di.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mustafakocer.core_database.dao.RemoteKeyDao
import com.mustafakocer.core_database.pagination.RemoteKey
import com.mustafakocer.core_database_contract.DatabaseConstants
import com.mustafakocer.feature_movies.list.data.local.dao.MovieListDao
import com.mustafakocer.feature_movies.list.data.local.entity.MovieListEntity

/**
 * Uygulamanın nihai ve tek veritabanı sınıfı.
 * Bu sınıf, :di modülünde bulunur çünkü tüm feature modüllerine erişimi olan tek yer burasıdır.
 * Tüm Entity'leri ve DAO'ları burada listeleriz.
 */

@Database(
    entities = [
        // Core Entities
        RemoteKey::class,

        // Feature:Movies Entities
        MovieListEntity::class,

        // Gelecekteki diğer feature'ların entity'leri buraya eklenecek...
    ],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = false // Portfolyo projesi için false kalabilir, productta değiştirmeyi unutma
)
abstract class AppDatabase : RoomDatabase() {

    // Gerekli tüm DAO'ları burada abstract fonksiyon olarak tanımla
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun movieListDao(): MovieListDao
// Gelecekteki diğer feature'ların DAO'ları buraya eklenecek...
}
