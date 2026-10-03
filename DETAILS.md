# DETAILS.md

## Architectural Decisions & Rationale

This document provides in-depth explanations of the architectural decisions, patterns, and best practices implemented in the Movie Discovery App. All statements are based on actual code and project structure.

---

### 1. Architecture & Module Structure

**Why this structure?**
- The app is small, so a few modules and clear packages are enough
- Fewer moving parts: faster builds, less boilerplate, easier to read
- The project began as a multi-module learning exercise (11 modules, MVI) and was deliberately simplified

**How is it implemented?**
- Three Gradle modules: `app` (entry point + every feature), `core-data` (data foundation), `core-ui` (theme and shared UI)
- Inside `app`, code is organized package-by-feature: `feature/home`, `feature/details`, `feature/search`, ...
- Layers are `UI (Screen) → ViewModel → [UseCase] → Repository → data source`; dependencies point one way

**Best Practices Applied:**
- Screens never touch Retrofit/Room; they only see `UiState`
- Repositories are plain classes (no interface with a single implementation)
- A UseCase exists only when it contains real logic (`GetHomeScreenDataUseCase` merges four category streams)

**Pitfalls Avoided:**
- No one-line "wrapper" UseCases and no interface-per-class ceremony
- No feature-to-feature dependencies: features only share `feature/movies/shared` and the core modules

---

### 2. MVVM with Unidirectional Data Flow

**Why MVVM instead of MVI?**
- MVI added a Contract (State/Event/Effect), a base ViewModel and event-to-effect round trips for every screen
- Plain MVVM gives the same predictable, one-directional flow with far less code

**How is it implemented?**
- Each ViewModel owns a private `MutableStateFlow<XUiState>` and exposes it as `StateFlow`
- User actions are ViewModel functions (`onRefresh()`, `onQueryChange()`), not event objects
- State flows down, actions flow up: the `Route` collects state with `collectAsStateWithLifecycle` and passes it plus callbacks to a stateless `Screen`
- One-shot events (open browser, restart activity, navigate after login) are **state fields** that the route consumes in a `LaunchedEffect`; the ViewModel resets them when needed. Unlike a `SharedFlow`, they cannot be lost while nothing is collecting
- Paging flows (`Flow<PagingData>`) are separate ViewModel properties, never part of a state class
- Pure UI actions (share intent, hiding the keyboard, navigation) are handled in the route and never go through the ViewModel

**Best Practices Applied:**
- Immutable `UiState` data classes, updated with `StateFlow.update`
- State hoisting: `Screen` composables are stateless and previewable
- ViewModels are unit tested with fake flows and virtual time

**Pitfalls Avoided:**
- No base ViewModel hierarchy and no forced `isLoading/error` fields on screens that do not need them
- No pass-through events that only turn into navigation effects

---

### 3. Dependency Injection (Hilt)

**Why Hilt?**
- Scalable, testable, and boilerplate-free DI

**How is it implemented?**
- `@HiltAndroidApp` in Application, `@HiltViewModel` for ViewModels
- Repositories and use cases are `@Inject constructor` classes (`@Singleton` where shared), so they need no module
- `@Module` + `@Provides` only for things Hilt cannot construct: Retrofit/OkHttp, API services, Room database and DAOs, DataStore

**Best Practices Applied:**
- No manual dependency graph
- Singleton scope for stateless/shared resources

**Pitfalls Avoided:**
- No service locator anti-pattern
- No `@Binds` boilerplate for interfaces that only have one implementation

---

### 4. Type-Safe Navigation

**Why?**
- Prevents runtime navigation errors (typed destinations and arguments)
- Keeps screens independent of the `NavController`

**How is it implemented?**
- Routes are `@Serializable` objects/data classes in `navigation/` (e.g. `MovieDetailsScreen(movieId)`)
- Each feature exposes a `NavGraphBuilder` extension (`splashNavGraph`, `moviesNavGraph`, `authNavGraph`); `AppNavHost` composes them
- A `Route` composable takes plain lambdas (`onNavigateToMovieDetails: (Int) -> Unit`), and the nav graph implements them with `navController`

**Best Practices Applied:**
- No string-based routes
- ViewModels read route arguments from `SavedStateHandle` using the route's property name (`MovieDetailsScreen::movieId.name`)

**Pitfalls Avoided:**
- No `NavController` passed into screens or ViewModels
- No navigation-contract interfaces that only forward calls

---

### 5. Unified Error Handling (AppException)

**Why a unified error model?**
- Consistent error handling across all layers
- User-friendly error messages surfaced to UI

**How is it implemented?**
- All errors are wrapped in `AppException`
- UI observes error state and displays via core-ui components

**Best Practices Applied:**
- No leaking of raw exceptions to UI
- Centralized error mapping

**Pitfalls Avoided:**
- No duplicated error handling logic
- No cryptic error messages for users

---

### 6. Offline-First & Pagination

**Why offline-first?**
- Fast loading, better UX, resilience to network issues

**How is it implemented?**
- Room DB for caching movie data
- Paging 3 for efficient, paginated data loading
- Home/list screens always try cache first, then network

**Best Practices Applied:**
- No blocking UI for network
- Data always available if previously loaded

**Pitfalls Avoided:**
- No empty screens on network loss
- No redundant network calls

---

### 7. Language & API Key Interceptors

**Why interceptors?**
- Ensures all network requests include correct language and API key
- Centralizes request modification logic

**How is it implemented?**
- OkHttp interceptors for language and API key
- Language can be changed at runtime, reflected in API calls

**Best Practices Applied:**
- No manual query param handling in each request
- Language change is instant and global

**Pitfalls Avoided:**
- No inconsistent language in API responses
- No missing API key errors

---

### 8. Language Architecture

**Why enum-based language support?**
- Add a new language with a single line
- Ensures compile-time safety and consistency

**How is it implemented?**
- Supported languages are defined as enums
- UI and API language are synchronized
- Language can be changed from settings instantly

**Best Practices Applied:**
- No hardcoded language codes
- No duplicated language logic

**Pitfalls Avoided:**
- No missed translations
- No inconsistent language state

---

### 9. Persistent Auth & Token Management

**Why persistent login?**
- Seamless user experience, no repeated logins

**How is it implemented?**
- The session id is stored in DataStore (app-private storage, not additionally encrypted)
- On app launch, `SplashViewModel` checks the stored session and routes to Home or Welcome

**Best Practices Applied:**
- No token in memory only
- Session id, request token and API key are masked in debug network logs (`redactSecrets`)

**Pitfalls Avoided:**
- No forced logout on app restart
- No token leaks

---

### 10. File Structure & Naming

**Why strict file structure?**
- Makes codebase easy to navigate and maintain

**How is it implemented?**
- Consistent naming conventions
- Feature-based and layer-based folder organization

**Best Practices Applied:**
- No mixed responsibilities in folders
- Easy to find any file by feature or layer

**Pitfalls Avoided:**
- No "misc" or "utils" dumping grounds
- No ambiguous file names

---

## Code Examples & Deep Dives

### 1. MVVM Example (Settings screen)

**ViewModel: one `UiState`, plain functions, one-shot event as state:**
```kotlin
data class SettingsUiState(
    val currentTheme: ThemePreference = ThemePreference.SYSTEM,
    val currentLanguage: LanguagePreference = LanguagePreference.ENGLISH,
    val isSaving: Boolean = false,
    val error: AppException? = null,
    val restartRequired: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val languageRepository: LanguageRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onLanguageSelected(language: LanguagePreference) {
        val state = _uiState.value
        if (language == state.currentLanguage || state.isSaving) return
        save(onSaved = { _uiState.update { it.copy(restartRequired = true) } }) {
            languageRepository.setLanguage(language)
        }
    }

    fun onRestartHandled() = _uiState.update { it.copy(restartRequired = false) }
    // ...
}
```

**Route: collects state, consumes the one-shot flag, stays free of business logic:**
```kotlin
@Composable
fun SettingsRoute(onNavigateUp: () -> Unit, onLanguageChanged: () -> Unit,
                  viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.restartRequired) {
        if (state.restartRequired) {
            viewModel.onRestartHandled()
            onLanguageChanged()
        }
    }

    SettingsScreen(
        state = state,
        onBackClick = onNavigateUp,
        onThemeSelected = viewModel::onThemeSelected,
        onLanguageSelected = viewModel::onLanguageSelected,
        // ...
    )
}
```

---

### 2. Dependency Injection (Hilt) Example

**A repository needs no module, just `@Inject`:**
```kotlin
@Singleton
class MovieDetailsRepository @Inject constructor(
    private val movieApiService: MovieApiService,
    private val languageRepository: LanguageRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getMovieDetails(movieId: Int): Flow<Resource<MovieDetails>> =
        languageRepository.languageFlow.flatMapLatest {
            safeApiCall { movieApiService.getMovieDetails(movieId) }
                .map { resource -> resource.mapSuccess { dto -> dto.toDomain() } }
        }
}
```

**`@Provides` only for what Hilt cannot construct:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object MovieNetworkModule {
    @Provides @Singleton
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService =
        retrofit.create(MovieApiService::class.java)
}
```

---

### 3. Type-Safe Navigation Example

**Routes are plain `@Serializable` types:**
```kotlin
@Serializable
data class MovieDetailsScreen(val movieId: Int)
```

**The nav graph turns navigation into lambdas; the route knows nothing about `NavController`:**
```kotlin
fun NavGraphBuilder.splashNavGraph(navController: NavController) {
    navigation<SplashFeatureGraph>(startDestination = SplashScreen) {
        composable<SplashScreen> {
            SplashRoute(
                onNavigateToHome = {
                    navController.navigate(MoviesFeatureGraph) {
                        popUpTo(SplashFeatureGraph) { inclusive = true }
                    }
                },
                onNavigateToWelcome = { /* ... */ },
            )
        }
    }
}
```

**AppNavHost composes the graphs:**
```kotlin
@Composable
fun AppNavHost(navController: NavHostController, ...) {
    NavHost(navController = navController, startDestination = startDestination) {
        splashNavGraph(navController)
        moviesNavGraph(navController, onLanguageChanged = { activity?.recreate() })
        authNavGraph(navController)
    }
}
```

---

### 3b. Testing a ViewModel

Repositories are replaced with Mockito mocks that return a controllable `MutableSharedFlow`, and `MainDispatcherRule` swaps `Dispatchers.Main`:
```kotlin
@Test
fun `loading with cached content does not flash the skeleton`() = runTest {
    val viewModel = createViewModel()
    homeData.emit(Resource.Success(content))

    homeData.emit(Resource.Loading)

    assertFalse(viewModel.uiState.value.isLoading)
}
```

---

### 4. API Key & Language Interceptors

**ApiKeyInterceptor:**
```kotlin
@Singleton
class ApiKeyInterceptor @Inject constructor(
    private val configProvider: NetworkConfigProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newUrl = chain.request().url.newBuilder()
            .addQueryParameter("api_key", configProvider.apiKey)
            .build()
        val newRequest = chain.request().newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}
```

**LanguageInterceptor:**
```kotlin
@Singleton
class LanguageInterceptor @Inject constructor(
    private val languageProvider: LanguageProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.newBuilder()
            .addQueryParameter("language", languageProvider.getLanguageParam())
            .build()
        val newRequest = chain.request().newBuilder().url(url).build()
        return chain.proceed(newRequest)
    }
}
```

---

### 5. Enum-Based Language Architecture

**LanguagePreference enum:**
```kotlin
enum class LanguagePreference(val code: String, val apiParam: String, val displayName: String, val flagResourceName: String) {
    ENGLISH("en", "en-US", "English", "flag_us"),
    TURKISH("tr", "tr-TR", "Türkçe", "flag_tr"),
    // ... other languages
    companion object {
        val DEFAULT = ENGLISH
        fun fromString(value: String?): LanguagePreference =
            try { valueOf(value ?: DEFAULT.name) } catch (e: IllegalArgumentException) { DEFAULT }
        fun getAllLanguages(): List<LanguagePreference> = entries
    }
}
```

---

### 6. Offline-First & Paging 3 Example

**BaseDao for Room:**
```kotlin
@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: T): Long
    @Upsert
    suspend fun upsert(entity: T)
    // ...
}
```

**RemoteKey for Paging:**
```kotlin
@Entity(tableName = "remote_keys", primaryKeys = ["query", "language"])
data class RemoteKey(
    val query: String,
    val language: String,
    @ColumnInfo(name = "current_page") val currentPage: Int,
    @ColumnInfo(name = "next_key") val nextKey: String? = null,
    @Embedded(prefix = "cache_") val cache: CacheMetadata,
    // ...
)
```

---

### 7. Unified Error Handling Example

**AppException (domain):**
```kotlin
sealed class AppException(...) : Exception(...) {
    sealed class Network : AppException { data class NoInternet(...) : Network(...) }
    sealed class Api : AppException { data class Unauthorized(...) : Api(...) }
    // ...
    data class Unknown(...) : AppException(...)
}
```

**AppExceptionMapper (UI):**
```kotlin
@Composable
fun AppException.toErrorInfo(): ErrorInfo = when (this) {
    is AppException.Network.NoInternet -> ErrorInfo(title = ..., description = ..., ...)
    is AppException.Api.Unauthorized -> ErrorInfo(title = ..., description = ..., ...)
    // ...
}
```

**ErrorScreen (UI):**
```kotlin
@Composable
fun ErrorScreen(error: ErrorInfo, onRetry: (() -> Unit)? = null, onNavigateBack: (() -> Unit)? = null) {
    // ... shows error icon, title, description, retry/go back buttons
}
```

---

### 8. Persistent Auth & Token Management

**SessionManager (DataStore):**
```kotlin
@Singleton
class SessionManager @Inject constructor(private val dataStore: DataStore<Preferences>) {
    val sessionIdFlow: Flow<String?> = dataStore.data.map { it[PreferenceKeys.SESSION_ID] }
    suspend fun saveSessionId(sessionId: String) { dataStore.edit { it[PreferenceKeys.SESSION_ID] = sessionId } }
    suspend fun clearSessionId() { dataStore.edit { it.remove(PreferenceKeys.SESSION_ID) } }
}
```

**LanguageRepository (DataStore):**
```kotlin
@Singleton
class LanguageRepository(private val dataStore: DataStore<Preferences>) {
    val languageFlow: Flow<LanguagePreference> = dataStore.data.map { ... }
    suspend fun setLanguage(language: LanguagePreference) { dataStore.edit { it[PreferenceKeys.LANGUAGE_PREFERENCE] = language.name } }
}
```

---

### 9. Shimmer Loading Animation for Network Data

**Why shimmer loading?**
- Provides a visually appealing, modern loading state instead of blank screens or spinners
- Sets user expectations and improves perceived performance
- Consistent experience across all screens that fetch data from the network

**How is it implemented?**
- A reusable `ShimmerBrush` composable creates an animated gradient brush for shimmer effects:

```kotlin
@Composable
fun ShimmerBrush(showShimmer: Boolean = true, targetValue: Float = 1000f): Brush {
    if (!showShimmer) {
        return Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
    }
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.0f),
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.0f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_animation"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnimation.value, y = translateAnimation.value)
    )
}
```

- The `ShimmerLoadingScreen` composable displays a dynamic list of shimmering placeholders, automatically filling the screen:

```kotlin
@Composable
fun ShimmerLoadingScreen(
    modifier: Modifier = Modifier,
    itemHeight: Dp,
    itemWidth: Dp,
    orientation: Orientation = Orientation.Vertical,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    skeletonContent: @Composable () -> Unit,
) {
    // ... calculates itemCount to fill the screen ...
    if (orientation == Orientation.Vertical) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
            userScrollEnabled = false
        ) {
            items(itemCount) {
                skeletonContent()
            }
        }
    } else {
        LazyRow(
            modifier = modifier.fillMaxSize(),
            contentPadding = contentPadding,
            userScrollEnabled = false
        ) {
            items(itemCount) {
                skeletonContent()
            }
        }
    }
}
```

**Architectural Note:**
- The shimmer logic is fully decoupled and reusable, reducing boilerplate in feature screens
- The slot-based API allows any placeholder shape, adapting to any device size and orientation
- All network-fetched data screens use this shimmer loading state, ensuring a polished and consistent UX

---

## Summary

This section provided real code samples and deep dives for each architectural highlight. All examples are directly taken from the project and reflect the actual implementation and best practices in use. 
