package com.mustafakocer.core_database_contract
//
///**
// * Veritabanı oluşturma sürecine katkıda bulunan feature modülleri için bir sözleşme.
// * Her feature modülü, bu interface'i implemente eden bir sınıf sağlayarak
// * kendi Entity'lerini ve TypeConverter'larını merkezi veri tabanına kaydettirir.
// */
//interface DatabaseContributor {
//    /**
//     * Bu modülün veritabanına ekleyeceği tüm @Entity sınıflarının bir listesini döndürür.
//     */
//    fun getEntities(): List<Class<*>>
//
//    /**
//     * Bu modülün veritabanına ekleyeceği tüm @TypeConverter sınıflarının bir listesini döndürür.
//     */
//    fun getTypeConverters(): List<Class<*>>
//
//    /**
//     * Bu özelliğin adını döndürür (debugging ve loglama için).
//     */
//    fun getFeatureName(): String
//}

/**
 *  * EDIT: Bu dosyayı silebilirsin. Bu dosya Room kütüphanesinin neden generic property'ler ile kullanılmayacağını gösteren yapıydı.
 *  * Room'un compile time'da AppDatabase'ye eklenecek tüm typeConverters'ları ve Entities'leri doğası gereği bilmesi gerekiyor.
 *  * Yani generic bir yapıyı Room'da kuramıyoruz.
 *  *  Eğer başka bir Database kütüphanesi kullanacak olursan bu yapıyı kullanabilirsin.
 *  */