# 📚 Technical Implementation Details
## Comprehensive Code Examples & Architectural Decisions

> **Deep dive into the technical implementation details, code examples, and architectural patterns used across the portfolio projects.**

---

## 🎬 **Movie Discovery App - Implementation Details**

### **🏗️ Advanced Modularization Strategy**

**Project Structure:**
```
📱 Single Activity Architecture
├── :app (Orchestration Layer)
│   ├── Navigation Coordination
│   ├── Database Centralization  
│   └── Global Configuration
├── 🎬 :feature-movies (Business Logic)
│   ├── Home, Details, Search, Settings
│   ├── Domain Models & Use Cases
│   └── Repository Implementations
├── 🔐 :feature-auth (Authentication)
│   ├── TMDB OAuth Integration
│   ├── Session Management
│   └── Deep Link Handling
├── ⚡ :feature-splash (Smart Launcher)
│   └── Parallel Task Execution
└── 🏗️ Core Modules (Infrastructure)
    ├── :core-ui (Design System)
    ├── :core-network (HTTP Layer)
    ├── :core-database (Data Persistence)
    └── :core-preferences (Settings)
```

### **🔐 Authentication Deep Link Solution**

**Challenge:** TMDB authentication requires external browser redirect while maintaining Single Activity Pattern.

**Solution Implementation:**

```kotlin
// 1. Invisible Activity for Deep Link Handling
class AuthCallbackActivity : ComponentActivity() {
    @Inject
    lateinit var authCallbackHandler: AuthCallbackHandler
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val token = intent.data?.getQueryParameter("request_token")
        token?.let { authCallbackHandler.onNewTokenReceived(it) }
        
        // Bring main app to front and self-destruct
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        })
        finish()
    }
}

// 2. Reactive Token Handler
@Singleton
class AuthCallbackHandler @Inject constructor() {
    private val _tokenFlow = MutableSharedFlow<String>(replay = 1)
    val tokenFlow = _tokenFlow.asSharedFlow()
    
    fun onNewTokenReceived(token: String) {
        _tokenFlow.tryEmit(token)
    }
}

// 3. ViewModel Integration
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authCallbackHandler: AuthCallbackHandler,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    init {
        // Observe token reception
        viewModelScope.launch {
            authCallbackHandler.tokenFlow.collect { token ->
                processAuthToken(token)
            }
        }
    }
    
    private suspend fun processAuthToken(token: String) {
        val sessionId = authRepository.createSession(token)
        // Navigate to authenticated area
    }
}
```

**Why This Solution is Advanced:**
- 🎯 **Preserves Architecture**: No violation of Single Activity Pattern
- ⚡ **Race Condition Safe**: SharedFlow with replay buffer ensures token delivery
- 🧪 **Testable**: Clean separation enables easy unit testing
- 🔄 **Reactive**: Seamlessly integrates with existing Flow-based architecture

### **🌍 Performance-Optimized Language System**

**Challenge:** Synchronous language access needed in network interceptors without blocking.

```kotlin
// Language Provider with Memory Cache
@Singleton
class LanguageProvider @Inject constructor(
    languageRepository: LanguageRepository,
    @ApplicationScope private val scope: CoroutineScope
) {
    @Volatile
    private var currentLanguageParam: String = "en-US"
    
    init {
        // Background observation of language changes
        scope.launch {
            languageRepository.languageFlow.collect { newLanguage ->
                currentLanguageParam = newLanguage.apiParam
            }
        }
    }
    
    // Synchronous access for interceptors
    fun getLanguageParam(): String = currentLanguageParam
}

// Network Interceptor Implementation
class LanguageInterceptor @Inject constructor(
    private val languageProvider: LanguageProvider
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val modifiedUrl = originalRequest.url.newBuilder()
            .addQueryParameter("language", languageProvider.getLanguageParam())
            .build()
        
        val modifiedRequest = originalRequest.newBuilder()
            .url(modifiedUrl)
            .build()
        
        return chain.proceed(modifiedRequest)
    }
}
```

**Advanced Concepts:**
- 🚀 **Zero-Cost Abstraction**: Memory-cached language for interceptor performance
- 🔄 **Reactive Updates**: Background observation of language changes
- 🧵 **Thread Safety**: @Volatile ensures cross-thread visibility
- 📈 **Performance Critical**: Synchronous access without coroutine overhead

### **🛡️ Centralized Error Management**

```kotlin
// Hierarchical Exception System
sealed class AppException(
    open val technicalMessage: String? = null,
    override val cause: Throwable? = null
) : Exception(technicalMessage, cause) {
    
    // Network-related errors
    sealed class Network : AppException() {
        data class NoInternet(override val cause: Throwable? = null) : Network()
        data class Timeout(override val cause: Throwable? = null) : Network()
        data class ServerUnreachable(override val cause: Throwable? = null) : Network()
    }
    
    // API-related errors
    sealed class Api(val httpCode: Int) : AppException() {
        data class Unauthorized(override val cause: Throwable? = null) : Api(401)
        data class Forbidden(override val cause: Throwable? = null) : Api(403)
        data class NotFound(override val cause: Throwable? = null) : Api(404)
        data class ServerError(val code: Int, override val cause: Throwable? = null) : Api(code)
    }
    
    // Local data errors
    sealed class Data : AppException() {
        data class DatabaseError(override val cause: Throwable) : Data()
        data class SerializationError(override val cause: Throwable) : Data()
    }
    
    // Business logic errors
    sealed class Business : AppException() {
        object InvalidCredentials : Business()
        object SessionExpired : Business()
        data class ValidationFailed(val field: String) : Business()
    }
}

// Error Mapper
@Singleton
class ErrorMapper @Inject constructor(@ApplicationContext private val context: Context) {
    
    fun mapThrowable(throwable: Throwable): AppException {
        return when (throwable) {
            is UnknownHostException -> AppException.Network.NoInternet(throwable)
            is SocketTimeoutException -> AppException.Network.Timeout(throwable)
            is ConnectException -> AppException.Network.ServerUnreachable(throwable)
            is HttpException -> mapHttpException(throwable)
            is SQLException -> AppException.Data.DatabaseError(throwable)
            is JsonSyntaxException -> AppException.Data.SerializationError(throwable)
            else -> AppException.Unknown(throwable.message, throwable)
        }
    }
    
    private fun mapHttpException(exception: HttpException): AppException.Api {
        return when (exception.code()) {
            401 -> AppException.Api.Unauthorized(exception)
            403 -> AppException.Api.Forbidden(exception)
            404 -> AppException.Api.NotFound(exception)
            in 500..599 -> AppException.Api.ServerError(exception.code(), exception)
            else -> AppException.Api.ClientError(exception.code(), exception)
        }
    }
    
    fun getErrorMessage(exception: AppException): String {
        return when (exception) {
            is AppException.Network.NoInternet -> context.getString(R.string.error_no_internet)
            is AppException.Network.Timeout -> context.getString(R.string.error_timeout)
            is AppException.Api.Unauthorized -> context.getString(R.string.error_unauthorized)
            is AppException.Business.SessionExpired -> context.getString(R.string.error_session_expired)
            else -> context.getString(R.string.error_generic)
        }
    }
}
```

### **🔄 Smart Resource Wrapper**

```kotlin
// Resource Wrapper for API Responses
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val exception: AppException) : Resource<Nothing>()
    
    // Utility extensions
    inline fun onSuccess(action: (T) -> Unit): Resource<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (AppException) -> Unit): Resource<T> {
        if (this is Error) action(exception)
        return this
    }
    
    inline fun onLoading(action: () -> Unit): Resource<T> {
        if (this is Loading) action()
        return this
    }
}

// Safe API Call Function
fun <T> safeApiCall(
    errorMapper: ErrorMapper,
    apiCall: suspend () -> Response<T>
): Flow<Resource<T>> = flow {
    emit(Resource.Loading)
    
    try {
        val response = apiCall()
        if (response.isSuccessful && response.body() != null) {
            emit(Resource.Success(response.body()!!))
        } else {
            val exception = errorMapper.mapHttpError(response)
            emit(Resource.Error(exception))
        }
    } catch (throwable: Throwable) {
        val exception = errorMapper.mapThrowable(throwable)
        emit(Resource.Error(exception))
    }
}.flowOn(Dispatchers.IO)

// Repository Usage
@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService,
    private val errorMapper: ErrorMapper
) : MovieRepository {
    
    override fun getPopularMovies(): Flow<Resource<List<Movie>>> {
        return safeApiCall(errorMapper) {
            apiService.getPopularMovies()
        }.map { resource ->
            resource.mapSuccess { response ->
                response.results.map { dto -> dto.toDomainModel() }
            }
        }
    }
}
```

### **🎨 MVI Pattern Implementation**

```kotlin
// Contract Pattern for Feature
interface MovieListContract {
    data class State(
        val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        val error: AppException? = null,
        val isRefreshing: Boolean = false
    )
    
    sealed interface Event {
        object LoadMovies : Event
        object RefreshMovies : Event
        data class SearchMovie(val query: String) : Event
        data class SelectMovie(val movieId: Int) : Event
        object RetryLoading : Event
    }
    
    sealed interface Effect {
        data class NavigateToDetail(val movieId: Int) : Effect
        data class ShowError(val message: String) : Effect
        object ScrollToTop : Effect
    }
}

// Generic Base ViewModel
abstract class BaseViewModel<State, Event, Effect> : ViewModel() {
    abstract val uiState: StateFlow<State>
    abstract val uiEffect: SharedFlow<Effect>
    abstract fun onEvent(event: Event)
}

// Feature ViewModel Implementation
@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val errorMapper: ErrorMapper
) : BaseViewModel<MovieListContract.State, MovieListContract.Event, MovieListContract.Effect>() {
    
    private val _uiState = MutableStateFlow(MovieListContract.State())
    override val uiState = _uiState.asStateFlow()
    
    private val _uiEffect = MutableSharedFlow<MovieListContract.Effect>()
    override val uiEffect = _uiEffect.asSharedFlow()
    
    init {
        onEvent(MovieListContract.Event.LoadMovies)
    }
    
    override fun onEvent(event: MovieListContract.Event) {
        when (event) {
            is MovieListContract.Event.LoadMovies -> loadMovies()
            is MovieListContract.Event.RefreshMovies -> refreshMovies()
            is MovieListContract.Event.SearchMovie -> searchMovies(event.query)
            is MovieListContract.Event.SelectMovie -> selectMovie(event.movieId)
            is MovieListContract.Event.RetryLoading -> retryLoading()
        }
    }
    
    private fun loadMovies() {
        viewModelScope.launch {
            movieRepository.getPopularMovies().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            movies = resource.data,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = resource.exception
                        )
                        val errorMessage = errorMapper.getErrorMessage(resource.exception)
                        _uiEffect.tryEmit(MovieListContract.Effect.ShowError(errorMessage))
                    }
                }
            }
        }
    }
    
    private fun selectMovie(movieId: Int) {
        viewModelScope.launch {
            _uiEffect.tryEmit(MovieListContract.Effect.NavigateToDetail(movieId))
        }
    }
}
```

---

## 📚 **Recipe Pagination App - Advanced Implementation**

### **🚀 RemoteMediator Excellence**

```kotlin
@OptIn(ExperimentalPagingApi::class)
class RecipeRemoteMediator @Inject constructor(
    private val apiService: RecipeApiService,
    private val database: RecipeDatabase,
    private val recipeDao: RecipeDao,
    private val remoteKeyDao: RemoteKeyDao
) : RemoteMediator<Int, RecipeEntity>() {
    
    override suspend fun initialize(): InitializeAction {
        // Check if cache is stale (older than 1 hour)
        val cacheTimeout = TimeUnit.HOURS.toMillis(1)
        val lastUpdated = remoteKeyDao.getCreationTime()
        
        return if (lastUpdated != null && 
                   (System.currentTimeMillis() - lastUpdated) < cacheTimeout) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }
    
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RecipeEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey
                    prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                    nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }
            
            val apiResponse = apiService.getRecipes(
                page = page,
                limit = state.config.pageSize
            )
            
            val recipes = apiResponse.recipes
            val endOfPaginationReached = recipes.isEmpty()
            
            database.withTransaction {
                // Clear all tables in the database if this is a refresh
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDao.clearRemoteKeys()
                    recipeDao.clearAllRecipes()
                }
                
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                
                val keys = recipes.map {
                    RemoteKey(
                        recipeId = it.id.toString(),
                        prevKey = prevKey,
                        nextKey = nextKey,
                        createdAt = System.currentTimeMillis()
                    )
                }
                
                remoteKeyDao.insertAll(keys)
                recipeDao.insertAll(recipes.map { it.toEntity() })
            }
            
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }
    
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, RecipeEntity>): RemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { recipe ->
                remoteKeyDao.getRemoteKeyByRecipeId(recipe.id.toString())
            }
    }
    
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, RecipeEntity>): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { recipe ->
                remoteKeyDao.getRemoteKeyByRecipeId(recipe.id.toString())
            }
    }
    
    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, RecipeEntity>
    ): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.toString()?.let { recipeId ->
                remoteKeyDao.getRemoteKeyByRecipeId(recipeId)
            }
        }
    }
}
```

### **📊 Database Entities & Relations**

```kotlin
// Recipe Entity with Relationships
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val difficulty: String,
    val cuisine: String,
    val caloriesPerServing: Int,
    val rating: Double,
    val reviewCount: Int,
    val createdAt: Long = System.currentTimeMillis()
)

// Ingredients Entity
@Entity(
    tableName = "ingredients",
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: Int,
    val name: String,
    val quantity: String
)

// Instructions Entity
@Entity(
    tableName = "instructions",
    foreignKeys = [
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InstructionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val recipeId: Int,
    val stepNumber: Int,
    val instruction: String
)

// Remote Key Entity for Pagination
@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey val recipeId: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val createdAt: Long = System.currentTimeMillis()
)

// Recipe with Relations
data class RecipeWithDetails(
    @Embedded val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<IngredientEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val instructions: List<InstructionEntity>
)
```

### **🏛️ Repository Implementation with Paging**

```kotlin
@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val apiService: RecipeApiService,
    private val database: RecipeDatabase,
    private val errorMapper: ErrorMapper
) : RecipeRepository {
    
    override fun getRecipes(): Flow<PagingData<Recipe>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            remoteMediator = RecipeRemoteMediator(
                apiService = apiService,
                database = database,
                recipeDao = recipeDao,
                remoteKeyDao = database.remoteKeyDao()
            ),
            pagingSourceFactory = { recipeDao.getAllRecipesPagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomainModel() }
        }
    }
    
    override suspend fun getRecipeDetails(recipeId: Int): Flow<Resource<RecipeDetail>> {
        return flow {
            emit(Resource.Loading)
            
            try {
                // First try to get from local database
                val localRecipe = recipeDao.getRecipeWithDetails(recipeId)
                if (localRecipe != null) {
                    emit(Resource.Success(localRecipe.toDomainModel()))
                }
                
                // Then fetch from API for latest data
                val apiResponse = apiService.getRecipeDetails(recipeId)
                if (apiResponse.isSuccessful && apiResponse.body() != null) {
                    val recipe = apiResponse.body()!!
                    
                    // Cache in database
                    database.withTransaction {
                        recipeDao.insertRecipeWithDetails(
                            recipe = recipe.toEntity(),
                            ingredients = recipe.ingredients.map { it.toEntity(recipeId) },
                            instructions = recipe.instructions.mapIndexed { index, instruction ->
                                instruction.toEntity(recipeId, index + 1)
                            }
                        )
                    }
                    
                    emit(Resource.Success(recipe.toDomainModel()))
                } else {
                    // If API fails but we have local data, keep showing it
                    if (localRecipe == null) {
                        val exception = errorMapper.mapHttpError(apiResponse)
                        emit(Resource.Error(exception))
                    }
                }
            } catch (throwable: Throwable) {
                val exception = errorMapper.mapThrowable(throwable)
                emit(Resource.Error(exception))
            }
        }.flowOn(Dispatchers.IO)
    }
    
    override fun searchRecipes(query: String): Flow<PagingData<Recipe>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { 
                RecipeSearchPagingSource(apiService, query, errorMapper) 
            }
        ).flow
    }
}
```

---

## 🛍️ **E-commerce App - Advanced Features**

### **🛒 Complex Cart Management**

```kotlin
// Cart Domain Model
data class CartItem(
    val id: String,
    val product: Product,
    val quantity: Int,
    val selectedVariant: ProductVariant? = null,
    val addedAt: Long = System.currentTimeMillis()
) {
    val totalPrice: Double
        get() = (selectedVariant?.price ?: product.price) * quantity
}

// Cart State Management
data class CartState(
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val itemCount: Int = 0,
    val isLoading: Boolean = false,
    val error: AppException? = null
) {
    val shippingCost: Double
        get() = if (totalAmount > 100.0) 0.0 else 9.99
    
    val finalAmount: Double
        get() = totalAmount + shippingCost
}

// Advanced Cart Repository
@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val productRepository: ProductRepository,
    private val userPreferences: UserPreferences
) : CartRepository {
    
    private val _cartFlow = MutableStateFlow(CartState())
    override val cartFlow = _cartFlow.asStateFlow()
    
    init {
        observeCartChanges()
    }
    
    private fun observeCartChanges() {
        viewModelScope.launch {
            cartDao.getAllCartItems().collect { cartEntities ->
                val cartItems = cartEntities.map { entity ->
                    val product = productRepository.getProduct(entity.productId)
                    entity.toDomainModel(product)
                }
                
                val totalAmount = cartItems.sumOf { it.totalPrice }
                val itemCount = cartItems.sumOf { it.quantity }
                
                _cartFlow.value = CartState(
                    items = cartItems,
                    totalAmount = totalAmount,
                    itemCount = itemCount
                )
            }
        }
    }
    
    override suspend fun addToCart(
        productId: String,
        quantity: Int,
        variantId: String?
    ): Result<Unit> {
        return try {
            val existingItem = cartDao.getCartItem(productId, variantId)
            
            if (existingItem != null) {
                // Update quantity of existing item
                val newQuantity = existingItem.quantity + quantity
                cartDao.updateQuantity(existingItem.id, newQuantity)
            } else {
                // Add new item to cart
                val cartEntity = CartItemEntity(
                    id = generateCartItemId(),
                    productId = productId,
                    quantity = quantity,
                    variantId = variantId,
                    addedAt = System.currentTimeMillis()
                )
                cartDao.insertCartItem(cartEntity)
            }
            
            // Track analytics
            trackCartEvent("add_to_cart", productId, quantity)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    override suspend fun updateQuantity(cartItemId: String, newQuantity: Int): Result<Unit> {
        return try {
            if (newQuantity <= 0) {
                removeFromCart(cartItemId)
            } else {
                cartDao.updateQuantity(cartItemId, newQuantity)
                Result.success(Unit)
            }
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    override suspend fun clearCart(): Result<Unit> {
        return try {
            cartDao.clearCart()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    override suspend fun saveCartForLater(): Result<Unit> {
        return try {
            val userId = userPreferences.getUserId()
            val cartItems = _cartFlow.value.items
            
            // Save cart to cloud for user
            val cartData = CartBackup(
                userId = userId,
                items = cartItems.map { it.toBackupModel() },
                savedAt = System.currentTimeMillis()
            )
            
            // Implementation depends on backend (Firebase, REST API, etc.)
            cloudRepository.saveCart(cartData)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
```

### **🔥 Firebase Integration**

```kotlin
// Firebase Repository Implementation
@Singleton
class FirebaseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val remoteConfig: FirebaseRemoteConfig,
    private val messaging: FirebaseMessaging,
    private val analytics: FirebaseAnalytics
) : FirebaseRepository {
    
    override suspend fun saveOrder(order: Order): Result<String> = withContext(Dispatchers.IO) {
        try {
            val orderRef = firestore.collection("orders").document()
            val orderData = order.toFirestoreMap()
            
            orderRef.set(orderData).await()
            
            // Track order completion
            analytics.logEvent("purchase") {
                param("transaction_id", orderRef.id)
                param("value", order.totalAmount)
                param("currency", "USD")
                param("item_count", order.items.size.toLong())
            }
            
            Result.success(orderRef.id)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    override fun observeProducts(): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection("products")
            .whereEqualTo("isActive", true)
            .orderBy("popularity", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                snapshot?.let { querySnapshot ->
                    val products = querySnapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject<ProductDto>()?.toDomainModel()?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null // Skip malformed documents
                        }
                    }
                    trySend(products)
                }
            }
        
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)
    
    override suspend fun getRemoteConfig(): Result<Map<String, Any>> {
        return try {
            remoteConfig.fetchAndActivate().await()
            
            val configMap = mapOf(
                "min_order_amount" to remoteConfig.getDouble("min_order_amount"),
                "free_shipping_threshold" to remoteConfig.getDouble("free_shipping_threshold"),
                "featured_categories" to remoteConfig.getString("featured_categories"),
                "maintenance_mode" to remoteConfig.getBoolean("maintenance_mode"),
                "app_update_required" to remoteConfig.getBoolean("app_update_required"),
                "payment_methods" to remoteConfig.getString("payment_methods")
            )
            
            Result.success(configMap)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    override suspend fun subscribeToTopic(topic: String): Result<Unit> {
        return try {
            messaging.subscribeToTopic(topic).await()
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
    
    override fun trackEvent(eventName: String, parameters: Map<String, Any>) {
        analytics.logEvent(eventName) {
            parameters.forEach { (key, value) ->
                when (value) {
                    is String -> param(key, value)
                    is Long -> param(key, value)
                    is Double -> param(key, value)
                    is Boolean -> param(key, if (value) 1L else 0L)
                    else -> param(key, value.toString())
                }
            }
        }
    }
}
```

### **💳 Payment Integration**

```kotlin
// Payment Processing
interface PaymentProcessor {
    suspend fun processPayment(paymentRequest: PaymentRequest): Result<PaymentResult>
    suspend fun refundPayment(transactionId: String, amount: Double): Result<RefundResult>
    fun validateCardDetails(cardDetails: CardDetails): ValidationResult
}

@Singleton
class StripePaymentProcessor @Inject constructor(
    private val stripeApi: StripeApiService,
    private val encryptionManager: EncryptionManager
) : PaymentProcessor {
    
    override suspend fun processPayment(paymentRequest: PaymentRequest): Result<PaymentResult> {
        return try {
            // Validate card details first
            val validation = validateCardDetails(paymentRequest.cardDetails)
            if (!validation.isValid) {
                return Result.failure(PaymentException.InvalidCard(validation.errors))
            }
            
            // Encrypt sensitive data
            val encryptedCard = encryptionManager.encrypt(paymentRequest.cardDetails)
            
            // Create payment intent
            val paymentIntent = stripeApi.createPaymentIntent(
                amount = (paymentRequest.amount * 100).toInt(), // Convert to cents
                currency = paymentRequest.currency,
                paymentMethodData = encryptedCard
            )
            
            // Confirm payment
            val confirmedPayment = stripeApi.confirmPaymentIntent(
                paymentIntentId = paymentIntent.id,
                paymentMethodId = paymentIntent.paymentMethodId
            )
            
            Result.success(
                PaymentResult(
                    transactionId = confirmedPayment.id,
                    status = PaymentStatus.COMPLETED,
                    amount = paymentRequest.amount,
                    processingFee = confirmedPayment.processingFee
                )
            )
        } catch (exception: Exception) {
            val paymentException = when (exception) {
                is SocketTimeoutException -> PaymentException.NetworkTimeout
                is HttpException -> when (exception.code()) {
                    402 -> PaymentException.InsufficientFunds
                    403 -> PaymentException.CardDeclined
                    else -> PaymentException.ProcessingError(exception.message())
                }
                else -> PaymentException.UnknownError(exception.message)
            }
            Result.failure(paymentException)
        }
    }
    
    override fun validateCardDetails(cardDetails: CardDetails): ValidationResult {
        val errors = mutableListOf<String>()
        
        // Validate card number using Luhn algorithm
        if (!isValidCardNumber(cardDetails.number)) {
            errors.add("Invalid card number")
        }
        
        // Validate expiry date
        if (!isValidExpiryDate(cardDetails.expiryMonth, cardDetails.expiryYear)) {
            errors.add("Invalid expiry date")
        }
        
        // Validate CVV
        if (!isValidCvv(cardDetails.cvv, cardDetails.number)) {
            errors.add("Invalid CVV")
        }
        
        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    
    private fun isValidCardNumber(number: String): Boolean {
        val cleanNumber = number.replace(" ", "").replace("-", "")
        if (cleanNumber.length < 13 || cleanNumber.length > 19) return false
        
        // Luhn algorithm implementation
        var sum = 0
        var alternate = false
        for (i in cleanNumber.length - 1 downTo 0) {
            var digit = cleanNumber[i].toString().toInt()
            if (alternate) {
                digit *= 2
                if (digit > 9) digit = (digit % 10) + 1
            }
            sum += digit
            alternate = !alternate
        }
        return sum % 10 == 0
    }
}
```

---

## 🔒 **Security Implementation Details**

### **🛡️ Data Encryption**

```kotlin
// Encryption Manager for Sensitive Data
@Singleton
class EncryptionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val keyAlias = "portfolio_app_key"
    private val transformation = "AES/GCM/NoPadding"
    
    init {
        generateOrGetKey()
    }
    
    fun encrypt(data: String): EncryptedData {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        
        val secretKey = keyStore.getKey(keyAlias, null) as SecretKey
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        
        return EncryptedData(
            data = Base64.encodeToString(encryptedBytes, Base64.DEFAULT),
            iv = Base64.encodeToString(iv, Base64.DEFAULT)
        )
    }
    
    fun decrypt(encryptedData: EncryptedData): String {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        
        val secretKey = keyStore.getKey(keyAlias, null) as SecretKey
        val cipher = Cipher.getInstance(transformation)
        val spec = GCMParameterSpec(128, Base64.decode(encryptedData.iv, Base64.DEFAULT))
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        
        val decryptedBytes = cipher.doFinal(Base64.decode(encryptedData.data, Base64.DEFAULT))
        return String(decryptedBytes, Charsets.UTF_8)
    }
    
    private fun generateOrGetKey() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }
}

// Secure Token Storage
@Singleton
class SecureTokenStorage @Inject constructor(
    private val encryptionManager: EncryptionManager,
    private val dataStore: DataStore<Preferences>
) {
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        val encryptedAccess = encryptionManager.encrypt(accessToken)
        val encryptedRefresh = encryptionManager.encrypt(refreshToken)
        
        dataStore.edit { preferences ->
            preferences[accessTokenKey] = "${encryptedAccess.data}:${encryptedAccess.iv}"
            preferences[refreshTokenKey] = "${encryptedRefresh.data}:${encryptedRefresh.iv}"
        }
    }
    
    suspend fun getAccessToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[accessTokenKey]?.let { encryptedString ->
                val parts = encryptedString.split(":")
                if (parts.size == 2) {
                    val encryptedData = EncryptedData(parts[0], parts[1])
                    encryptionManager.decrypt(encryptedData)
                } else null
            }
        }.first()
    }
    
    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(refreshTokenKey)
        }
    }
}
```

### **🔐 Certificate Pinning**

```kotlin
// Network Security Configuration
@Module
@InstallIn(SingletonComponent::class)
object NetworkSecurityModule {
    
    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            .add("api.themoviedb.org", "sha256/k1Hdw5sdSn5kh/gemLVSQD/P4i4IBQEY1tW4DoNpTbI=")
            .add("api.themoviedb.org", "sha256/18tkPyr2nckv4fgo0dhAkaUtJ2hu2831xlO2SKhq8dg=")
            .build()
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        certificatePinner: CertificatePinner,
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}

// Auth Interceptor with Token Management
class AuthInterceptor @Inject constructor(
    private val tokenStorage: SecureTokenStorage,
    private val authRepository: AuthRepository
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth for login/register endpoints
        if (originalRequest.url.encodedPath.contains("/auth/")) {
            return chain.proceed(originalRequest)
        }
        
        val accessToken = runBlocking { tokenStorage.getAccessToken() }
        
        val authenticatedRequest = originalRequest.newBuilder()
            .apply {
                accessToken?.let { token ->
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .build()
        
        val response = chain.proceed(authenticatedRequest)
        
        // Handle token refresh on 401
        if (response.code == 401 && accessToken != null) {
            response.close()
            
            synchronized(this) {
                // Check if token was already refreshed by another thread
                val currentToken = runBlocking { tokenStorage.getAccessToken() }
                if (currentToken != accessToken) {
                    // Token was refreshed, retry with new token
                    val newRequest = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $currentToken")
                        .build()
                    return chain.proceed(newRequest)
                }
                
                // Attempt token refresh
                val refreshResult = runBlocking { authRepository.refreshToken() }
                
                return if (refreshResult.isSuccess) {
                    // Retry with new token
                    val newToken = refreshResult.getOrNull()
                    val retryRequest = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $newToken")
                        .build()
                    chain.proceed(retryRequest)
                } else {
                    // Refresh failed, redirect to login
                    runBlocking { authRepository.logout() }
                    response
                }
            }
        }
        
        return response
    }
}
```

---

## 🧪 **Testing Strategy & Examples**

### **📝 Unit Testing Examples**

```kotlin
// ViewModel Testing with Coroutines
@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    @Mock
    private lateinit var movieRepository: MovieRepository
    
    @Mock
    private lateinit var errorMapper: ErrorMapper
    
    private lateinit var viewModel: MovieListViewModel
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = MovieListViewModel(movieRepository, errorMapper)
    }
    
    @Test
    fun `when loadMovies is called, should emit loading then success states`() = runTest {
        // Given
        val mockMovies = listOf(
            Movie(1, "Test Movie 1", "poster1.jpg"),
            Movie(2, "Test Movie 2", "poster2.jpg")
        )
        whenever(movieRepository.getPopularMovies()).thenReturn(
            flowOf(Resource.Success(mockMovies))
        )
        
        // When
        viewModel.onEvent(MovieListContract.Event.LoadMovies)
        
        // Then
        val states = mutableListOf<MovieListContract.State>()
        val job = launch {
            viewModel.uiState.collect { states.add(it) }
        }
        
        advanceUntilIdle()
        
        assertThat(states).hasSize(2)
        assertThat(states[0].isLoading).isTrue()
        assertThat(states[1].isLoading).isFalse()
        assertThat(states[1].movies).isEqualTo(mockMovies)
        assertThat(states[1].error).isNull()
        
        job.cancel()
    }
    
    @Test
    fun `when repository returns error, should emit error state and effect`() = runTest {
        // Given
        val exception = AppException.Network.NoInternet()
        val errorMessage = "No internet connection"
        whenever(movieRepository.getPopularMovies()).thenReturn(
            flowOf(Resource.Error(exception))
        )
        whenever(errorMapper.getErrorMessage(exception)).thenReturn(errorMessage)
        
        // When
        viewModel.onEvent(MovieListContract.Event.LoadMovies)
        
        // Then
        val effects = mutableListOf<MovieListContract.Effect>()
        val effectJob = launch {
            viewModel.uiEffect.collect { effects.add(it) }
        }
        
        advanceUntilIdle()
        
        assertThat(viewModel.uiState.value.error).isEqualTo(exception)
        assertThat(effects).contains(MovieListContract.Effect.ShowError(errorMessage))
        
        effectJob.cancel()
    }
}

// Repository Testing
class MovieRepositoryImplTest {
    
    @Mock
    private lateinit var apiService: MovieApiService
    
    @Mock
    private lateinit var movieDao: MovieDao
    
    @Mock
    private lateinit var errorMapper: ErrorMapper
    
    private lateinit var repository: MovieRepositoryImpl
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = MovieRepositoryImpl(apiService, movieDao, errorMapper)
    }
    
    @Test
    fun `getPopularMovies should return cached data first then fetch from API`() = runTest {
        // Given
        val cachedMovies = listOf(MovieEntity(1, "Cached Movie"))
        val apiMovies = MovieListResponse(
            results = listOf(MovieDto(1, "API Movie")),
            totalPages = 1
        )
        
        whenever(movieDao.getAllMovies()).thenReturn(flowOf(cachedMovies))
        whenever(apiService.getPopularMovies()).thenReturn(Response.success(apiMovies))
        
        // When
        val result = repository.getPopularMovies().toList()
        
        // Then
        assertThat(result).hasSize(2) // Loading + Success
        assertThat(result[0]).isInstanceOf(Resource.Loading::class.java)
        assertThat(result[1]).isInstanceOf(Resource.Success::class.java)
        
        val successResult = result[1] as Resource.Success
        assertThat(successResult.data).hasSize(1)
        assertThat(successResult.data[0].title).isEqualTo("API Movie")
        
        // Verify caching
        verify(movieDao).insertMovies(any())
    }
}
```

### **🔗 Integration Testing**

```kotlin
@RunWith(AndroidJUnit4::class)
@LargeTest
class DatabaseIntegrationTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: MovieDatabase
    private lateinit var movieDao: MovieDao
    
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MovieDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        movieDao = database.movieDao()
    }
    
    @After
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertAndRetrieveMovies() = runTest {
        // Given
        val movies = listOf(
            MovieEntity(1, "Movie 1", "poster1.jpg"),
            MovieEntity(2, "Movie 2", "poster2.jpg")
        )
        
        // When
        movieDao.insertMovies(movies)
        val retrievedMovies = movieDao.getAllMovies().first()
        
        // Then
        assertThat(retrievedMovies).hasSize(2)
        assertThat(retrievedMovies[0].title).isEqualTo("Movie 1")
        assertThat(retrievedMovies[1].title).isEqualTo("Movie 2")
    }
    
    @Test
    fun testCascadeDelete() = runTest {
        // Given
        val movie = MovieEntity(1, "Test Movie", "poster.jpg")
        val favorites = listOf(
            FavoriteEntity(1, 1, System.currentTimeMillis())
        )
        
        // When
        movieDao.insertMovies(listOf(movie))
        movieDao.insertFavorites(favorites)
        movieDao.deleteMovie(1)
        
        // Then
        val remainingMovies = movieDao.getAllMovies().first()
        val remainingFavorites = movieDao.getAllFavorites().first()
        
        assertThat(remainingMovies).isEmpty()
        assertThat(remainingFavorites).isEmpty() // Should be deleted by cascade
    }
}
```

---

**This detailed documentation provides comprehensive examples of the advanced patterns, implementations, and testing strategies used across all portfolio projects. Each code example demonstrates real-world problem-solving with enterprise-level solutions.**