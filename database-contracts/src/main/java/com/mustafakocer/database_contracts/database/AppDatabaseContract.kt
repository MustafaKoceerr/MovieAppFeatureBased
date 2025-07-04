package com.mustafakocer.database_contracts.database

import androidx.room.RoomDatabase
import com.mustafakocer.core_database.pagination.RemoteKey
// ==================== ABSTRACT DATABASE ====================

/**
 * Soyut veritabanı sınıfı
 * app modülünde concrete implementation yapılacak
 */
abstract class AppDatabaseContract : RoomDatabase() {
    // Feature DAOs - feature'lar kendi DAO'larını burada expose edecek
    // Bu methodlar app modülünde override edilecek

    // Core DAO'lar app modülünde doğrudan tanımlanacak
    // abstract fun remoteKeyDao(): RemoteKeyDao // ← KALDIRILDI
}