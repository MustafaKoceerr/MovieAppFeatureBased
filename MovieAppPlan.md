# MovieApp: MAD + Modern MVVM Planı

Analiz: `MovieAppAnalysis.txt`. Branch: `refactor/mad-mvvm`. Her adım ayrı commit, her adım sonunda `./gradlew assembleDebug` yeşil olmalı.

## Hedef
- MVI yerine basit MVVM: ViewModel başına `UiState` (`StateFlow`) + doğrudan fonksiyonlar.
- 11 modül → 3 modül: `:app`, `:core-data`, `:core-ui`.
- UseCase sadece gerçek iş mantığı varsa. Aksi halde ViewModel → Repository.

## Hedef mimari
```
:app       Application, MainActivity, NavHost, feature/{splash,auth,home,list,details,search,settings}, AppDatabase, DI
:core-data Resource/AppException, network, preferences (DataStore), database base (BaseDao, RemoteKey)
:core-ui   tema, ortak Compose bileşenleri, hata UI eşlemesi
```
`UI → ViewModel(UiState) → [UseCase?] → Repository → DataSource`

PagingData state içinde değil: `val movies: Flow<PagingData<Movie>>` (`cachedIn(viewModelScope)`).
Navigasyon composable'dan lambda ile; ViewModel'den geçmez.

## Adımlar
- [x] 0. Hazırlık: branch, `MovieAppAnalysis.txt`, `MovieAppPlan.md`
- [x] 1. Build temizliği: catalog, safeargs, çift room, API_KEY güvenli okuma, debug/release tekrarı
- [ ] 2. Core birleştirme: core-domain/network/preferences/database → `:core-data`; `core-android` ve `BaseUi*`/`UiContract` silinir; safeApiCall dispatcher inject; Gson → kotlinx-serialization
- [ ] 3. Feature'ları `:app`'e taşı (splash → auth → movies); `navigation-contracts` route'ları `:app`'e; NavActions → lambda; eski modülleri kaldır
- [ ] 4. MVI → MVVM, ekran ekran: Splash, Settings, Welcome, Account, Home, Details, List, Search
- [ ] 5. Domain sadeleştirme: tek satırlık UseCase'ler silinir; paging/data import'ları temizlenir; mapper tekrarları birleşir; hard-coded string'ler UI'a
- [ ] 6. Kalite/güvenlik: runBlocking, api_key log redaksiyonu, Log.d, yükleme state'i tek kaynak
- [ ] 7. Testler (Home, Details, Search VM) + @Preview
- [ ] 8. README.md / DETAILS.md güncelle

## Doğrulama
- Her adım: `./gradlew assembleDebug`
- Adım 4 sonrası: manuel akış (splash → welcome/login → home → details/list/search → settings, dil/tema)
- Adım 7 sonrası: `./gradlew testDebugUnitTest`
- Çalıştırma için `local.properties` içinde `API_KEY` gerekir.
