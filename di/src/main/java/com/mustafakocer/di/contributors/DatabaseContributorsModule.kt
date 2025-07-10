package com.mustafakocer.di.contributors

//
//import com.mustafakocer.core_database_contract.DatabaseContributor
//import com.mustafakocer.feature_movies.database.MoviesDatabaseContributor
//import dagger.Binds
//import dagger.Module
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import dagger.multibindings.IntoSet
//
//@Module
//@InstallIn(SingletonComponent::class)
//abstract class DatabaseContributorsModule {
//
//    @Binds
//    @IntoSet // Bu, Hilt'e sonucu tek bir Set<DatabaseContributor> içinde toplamasını söyler
//    abstract fun bindMoviesDatabaseContributor(
//        contributor: MoviesDatabaseContributor,
//    ): DatabaseContributor
//
//    // Gelecekte yeni bir feature (örn: :feature-profile) eklediğinde,
//    // onun Contributor'ını da buraya ekleyeceksin:
//    // @Binds
//    // @IntoSet
//    // abstract fun bindProfileDatabaseContributor(
//    //     contributor: ProfileDatabaseContributor
//    // ): DatabaseContributor
//}

/**
 *  * EDIT: Bu dosyayı silebilirsin. Bu dosya Room kütüphanesinin neden generic property'ler ile kullanılmayacağını gösteren yapıydı.
 *  * Room'un compile time'da AppDatabase'ye eklenecek tüm typeConverters'ları ve Entities'leri doğası gereği bilmesi gerekiyor.
 *  * Yani generic bir yapıyı Room'da kuramıyoruz.
 *  *  Eğer başka bir Database kütüphanesi kullanacak olursan bu yapıyı kullanabilirsin.
 *  */