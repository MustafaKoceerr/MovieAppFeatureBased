package com.mustafakocer.core_database_contract

/**
 * Uygulama genelindeki veritabanı sabitlerini barındıran merkezi nesne.
 * Bu, "magic string" ve "magic number" kullanımını önler.
 */
object DatabaseConstants {
    /**
     * Uygulama veritabanının dosya adı.
     */
    const val DATABASE_NAME = "movie_app_database.db"

    /**
     * Veritabanının mevcut şema versiyonu.
     * Şemada (Entity'lerde) bir değişiklik yapıldığında bu numara artırılmalıdır.
     */
    const val DATABASE_VERSION = 1
}