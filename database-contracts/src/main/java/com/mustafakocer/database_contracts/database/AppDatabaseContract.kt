package com.mustafakocer.database_contracts.database

import androidx.room.RoomDatabase
import com.mustafakocer.core_database.pagination.RemoteKey
// ==================== ABSTRACT DATABASE ====================

/**
 * Soyut veritabanı sınıfı
 * app modülünde concrete implementation yapılacak
 */
abstract class AppDatabaseContract : RoomDatabase() {

}