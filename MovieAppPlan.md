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
- [x] 2. Core birleştirme: core-domain/network/preferences/database → `:core-data` (paket adları korundu, interceptor sınıfları `interceptor/` paketine alındı); Gson → kotlinx-serialization. **Not:** `core-android` ve `BaseUi*`/`UiContract` MVI ViewModel'leri tarafından kullanıldığı için Adım 4 sonunda silinecek; `safeApiCall` dispatcher inject işi Adım 6'ya kaldı
- [x] 3. Feature'ları `:app`'e taşı (paketler `...movieappfeaturebasedclean.feature.{splash,auth,home,details,list,search,settings,movies}`); `navigation-contracts` route'ları `:app/navigation`'a; eski modüller kaldırıldı. **Not:** `NavActions` → lambda dönüşümü, her ekran Adım 4'te MVVM'e geçerken yapılacak (aynı dosyalara iki kez dokunmamak için)
- [x] 4. MVI → MVVM, ekran ekran: Splash, Settings, Welcome, Account, Home, Details, List, Search. Tek seferlik olaylar state olarak tüketiliyor, navigasyon lambda ile; `NavActions`, Contract dosyaları, `BaseViewModel`/`BaseUi*`/`UiContract` ve `:core-android` modülü silindi (`LocaleUtils` → `app/util`). Modüller: `:app`, `:core-data`, `:core-ui`
- [x] 5. Domain sadeleştirme: 7 UseCase silindi (yalnızca `GetHomeScreenDataUseCase` kaldı, gerçek birleştirme mantığı var); repository interface'leri kaldırılıp somut sınıf oldu, dil değişince yeniden yükleme repository'ye taşındı; auth'ta `LoginRepository`/`AccountRepository` → tek `AuthRepository`; mapper tekrarları tek yardımcıda birleşti; sabit İngilizce fallback'ler string resource'a (7 dil) taşındı
- [x] 6. Kalite/güvenlik: OkHttp loglarında `api_key`/`session_id`/`request_token` maskeleme (`redactSecrets`), `AuthCallbackActivity` `Log.d` kaldırıldı, `safeApiCall`'daki sabit `Dispatchers.IO` kaldırıldı (Retrofit suspend zaten main-safe), `SocketTimeoutException` → `Network.Timeout`. Yükleme state'i: `BaseUiState` kalkınca çift modelleme kendiliğinden bitti (`Resource.Loading` repository sinyali, `isLoading` UI state). **Yapılmadı:** `MainActivity.attachBaseContext` içindeki `runBlocking` (cihazda doğrulayamadığım için riskli; `AppCompatDelegate.setApplicationLocales` geçişi gerektirir, kullanıcı kararına bırakıldı)
- [x] 7. Testler: Home/Details/Search ViewModel (coroutines-test + Turbine + Mockito, 14 test) ve core-data (log maskeleme + hata eşleme, 8 test); `@Preview`: Home (içerik + yükleme), Details (içerik + yükleme), Settings, Account (misafir + giriş yapılmış), Splash, Search ilk ekranı. **Not:** Details/List ViewModel'leri route argümanını `toRoute` yerine `Screen::alan.name` ile okuyor (JVM testinde Android `Bundle` gerektirmemesi için); Mockito 5.20.0'a yükseltildi
- [x] 8. README.md / DETAILS.md güncelle

## Doğrulama
- Her adım: `./gradlew assembleDebug`
- Adım 4 sonrası: manuel akış (splash → welcome/login → home → details/list/search → settings, dil/tema)
- Adım 7 sonrası: `./gradlew testDebugUnitTest`
- Çalıştırma için `local.properties` içinde `API_KEY` gerekir.
