# DETAILS.md

## Architectural Decisions & Rationale

This document provides in-depth explanations of the architectural decisions, patterns, and best practices implemented in the Movie Discovery App. All statements are based on actual code and project structure.

---

### 1. Clean Architecture & Modularization

**Why Clean Architecture?**
- Enforces strict separation of concerns (domain, data, presentation)
- Makes the codebase scalable, testable, and maintainable
- Allows independent development and testing of features

**How is it implemented?**
- Each feature and core concern is a separate Gradle module
- Domain logic is isolated from data and UI layers
- Dependencies flow inward (UI → Domain, Data → Domain)

**Best Practices Applied:**
- No direct dependency from UI to data/network
- All business logic in domain layer
- Feature modules depend only on what they need

**Pitfalls Avoided:**
- No God classes or monolithic modules
- No tight coupling between features

---

### 2. MVI Pattern & UI Contracts

**Why MVI?**
- Ensures unidirectional data flow
- Makes UI state predictable and easy to debug
- Simplifies testing and maintenance

**How is it implemented?**
- All ViewModels implement a common `UiContract` interface
- State, events, and effects are managed via Kotlin Flows
- UI observes state/effects, sends events to ViewModel

**Best Practices Applied:**
- No mutable state in UI
- All user actions go through ViewModel

**Pitfalls Avoided:**
- No spaghetti event handling
- No inconsistent state updates

---

### 3. Dependency Injection (Hilt)

**Why Hilt?**
- Scalable, testable, and boilerplate-free DI
- Enables easy swapping of implementations (e.g., for testing)

**How is it implemented?**
- All modules use Hilt for providing dependencies
- `@HiltAndroidApp` in Application, `@HiltViewModel` for ViewModels
- `@Module` and `@Binds`/`@Provides` for interface-implementation binding

**Best Practices Applied:**
- No manual dependency graph
- Singleton scope for stateless/shared resources

**Pitfalls Avoided:**
- No service locator anti-pattern
- No manual DI setup in Activities/Fragments

---

### 4. Type-Safe Navigation (navigation-contracts)

**Why navigation-contracts?**
- Decouples navigation logic from feature modules
- Prevents runtime navigation errors (type-safe destinations)

**How is it implemented?**
- Each feature exposes its own navigation graph via contracts
- App module composes the navigation graphs
- Destinations are serializable objects, not string routes

**Best Practices Applied:**
- No string-based navigation
- No cross-feature navigation dependencies

**Pitfalls Avoided:**
- No navigation logic leaks between modules
- No fragile deep-linking

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
- Token is stored securely in DataStore
- On app launch, token is checked and user is auto-logged in if valid

**Best Practices Applied:**
- No token in memory only
- Secure, encrypted storage (if supported)

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

### 1. MVI & UI Contract Example

**UiContract interface:**
```kotlin
interface UiContract<State, Event, Effect> {
    val uiState: StateFlow<State>
    val uiEffect: SharedFlow<Effect>
    fun onEvent(event: Event)
}
```

**BaseViewModel implementation:**
```kotlin
abstract class BaseViewModel<State : BaseUiState, Event : BaseUiEvent, Effect : BaseUiEffect>(
    initialState: State,
) : ViewModel(), UiContract<State, Event, Effect> {
    private val _uiState = MutableStateFlow(initialState)
    override val uiState: StateFlow<State> = _uiState.asStateFlow()
    private val _uiEffect = MutableSharedFlow<Effect>(replay = 0, extraBufferCapacity = 1)
    override val uiEffect: SharedFlow<Effect> = _uiEffect.asSharedFlow()
    abstract override fun onEvent(event: Event)
    protected fun setState(reduce: State.() -> State) { _uiState.value = currentState.reduce() }
    protected fun sendEffect(effect: Effect) { viewModelScope.launch { _uiEffect.emit(effect) } }
}
```

---

### 2. Dependency Injection (Hilt) Example

**RepositoryModule with @Binds:**
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository
    // ... other bindings
}
```

---

### 3. Type-Safe Navigation Example

**AppNavHost decoupling navigation:**
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
