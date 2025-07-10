package com.mustafakocer.feature_movies.database

//import com.mustafakocer.core_database_contract.DatabaseContributor
//import com.mustafakocer.feature_movies.list.data.local.entity.MovieListEntity
//import javax.inject.Inject

/**
 * :feature-movies modülünün veritabanına yaptığı katkıları tanımlar.
 * DatabaseContributor sözleşmesini uygular.
 */
//class MoviesDatabaseContributor @Inject constructor() : DatabaseContributor {
//    override fun getEntities(): List<Class<*>> {
//        // Bu modülün veritabanına eklemek istediği tüm Entity'ler
//        return listOf(
//            MovieListEntity::class.java
//        )
//        // Gelecekte eklenecek diğer movie entity'leri buraya gelecek
//        // örn: MovieDetailEntity::class.java
//    }
//
//    override fun getTypeConverters(): List<Class<*>> {
//        // Bu modülün veritabanına eklemek istediği TypeConverter'lar
//        // Şu an için yok.
//        return emptyList()
//    }
//
//    override fun getFeatureName(): String {
//        return "movies"
//    }
//
//}


 /**
 *  * EDIT: Bu dosyayı silebilirsin. Bu dosya Room kütüphanesinin neden generic property'ler ile kullanılmayacağını gösteren yapıydı.
 *  * Room'un compile time'da AppDatabase'ye eklenecek tüm typeConverters'ları ve Entities'leri doğası gereği bilmesi gerekiyor.
 *  * Yani generic bir yapıyı Room'da kuramıyoruz.
  *  *  Eğer başka bir Database kütüphanesi kullanacak olursan bu yapıyı kullanabilirsin.
 *  */