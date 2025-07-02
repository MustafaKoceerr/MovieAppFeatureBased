package com.mustafakocer.feature_movies.home.domain.model

/**
 * Home screen'in tüm içeriği
 * Her kategori için ayrı section
 */
data class HomeContent(
    val nowPlayingSection: MovieSection,
    val popularSection: MovieSection,
    val topRatedSection: MovieSection,
    val upcomingSection: MovieSection,
) {
    /**
     * Tüm section'ların listesi (UI'da iterate etmek için)
     */
    val allSections: List<MovieSection>
        get() = listOf(nowPlayingSection, popularSection, topRatedSection, upcomingSection)

    /**
     * Herhangi bir section loading durumunda mı?
     */
    val isAnyLoading: Boolean
        get() = allSections.any { it.isLoading }

    /**
     * Herhangi bir section'da error var mı?
     */
    val hasAnyError: Boolean
        get() = allSections.any { it.error != null }

    /**
     * Tüm section'lar boş mu?
     */
    val isEmpty: Boolean
        get() = allSections.all { it.movies.isEmpty() }

    /**
     * ✅ CEVAP: EVET, uygun. Çünkü bu fonksiyonlar business logic değil, model-level convenience logic içeriyor.
     * NEDEN BU FONKSİYONLAR BUSINESS LOGIC DEĞİL?
     * updateSection(...) ve getSection(...):
     *
     * Sadece data class'ın kendi verileri üzerinde çalışıyor.
     *
     * Dış servislere çağrı yapmıyor, kullanıcı girdisi işlemiyor, domain kararları vermiyor.
     *
     * Yalnızca HomeContent objesinin iç yapısını immutably güncellemek veya içinden veri almak için var.
     *
     * Bu tür fonksiyonlar, modelin içsel davranışı (invariantlarını koruma, parçalarını kolayca yönetme) ile ilgilidir.
     *
     * NEDEN DOMAIN KATMANINDA OLMASI UYGUNDUR?
     * Domain Layer, sadece veri sınıfı değil, bu sınıfın bütünlüğünü koruyacak fonksiyonları da içerebilir.
     *
     * Eğer bu HomeContent nesnesi, sadece domain'de varsa ve başka yerde anlamlı değilse:
     *
     * Bu fonksiyonlar için ayrı bir Mapper, Helper ya da UseCase tanımlamak gereksiz abstraction olur.
     *
     * Ayrıca bu fonksiyonlar:
     *
     * Domain nesnesinin değiştirilemezliğini korur (copy kullanıyor).
     *
     * Domain nesnesinin kategori-tabanlı erişimini kolaylaştırır (getSection, updateSection).
     */
    /**
     * 🔸 Peki ne zaman uygunsuz olurdu?
     * Bu fonksiyonlar şunları yapsaydı:
     *
     * Uygunsuz Davranış	Neden
     * API çağrısı	Domain veri sınıfı UI ya da data layer'a sızmış olurdu
     * Veritabanı erişimi	UseCase'e taşınmalıydı
     * Kullanıcı input'u validasyonu	Bu HomeContent değil, genellikle UseCase’in sorumluluğudur
     * Yan etki üretme (Toast, Navigation, Loglama, vb.)	UI ya da application layer’da olmalı
     */

    /**
     * Update specific section (immutable update)
     */
    fun updateSection(categoryType: MovieCategoryType, newSection: MovieSection): HomeContent {
        return when (categoryType) {
            MovieCategoryType.NOW_PLAYING -> copy(nowPlayingSection = newSection)
            MovieCategoryType.POPULAR -> copy(popularSection = newSection)
            MovieCategoryType.TOP_RATED -> copy(topRatedSection = newSection)
            MovieCategoryType.UPCOMING -> copy(upcomingSection = newSection)
        }
    }

    /**
     * Get specific section by category
     */
    fun getSection(categoryType: MovieCategoryType): MovieSection {
        return when (categoryType) {
            MovieCategoryType.NOW_PLAYING -> nowPlayingSection
            MovieCategoryType.POPULAR -> popularSection
            MovieCategoryType.TOP_RATED -> topRatedSection
            MovieCategoryType.UPCOMING -> upcomingSection
        }
    }

    companion object {
        fun empty(): HomeContent = HomeContent(
            nowPlayingSection = MovieSection.empty(MovieCategoryType.NOW_PLAYING),
            popularSection = MovieSection.empty(MovieCategoryType.POPULAR),
            topRatedSection = MovieSection.empty(MovieCategoryType.TOP_RATED),
            upcomingSection = MovieSection.empty(MovieCategoryType.UPCOMING)
        )

        fun loading(): HomeContent = HomeContent(
            nowPlayingSection = MovieSection.loading(MovieCategoryType.NOW_PLAYING),
            popularSection = MovieSection.loading(MovieCategoryType.POPULAR),
            topRatedSection = MovieSection.loading(MovieCategoryType.TOP_RATED),
            upcomingSection = MovieSection.loading(MovieCategoryType.UPCOMING)
        )
    }
}