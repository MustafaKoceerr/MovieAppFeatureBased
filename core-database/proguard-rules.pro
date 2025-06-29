# Core Database Module Consumer ProGuard Rules
# These rules will be applied to consuming modules

# ==================== ROOM DATABASE ====================
# Keep Room generated classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.paging.**

# ==================== KOTLINX SERIALIZATION ====================
# Keep serialization annotations and classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep Serializable classes
-keep,allowobfuscation,allowshrinking class **.*$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,allowobfuscation,allowshrinking class **.*$$serializer {
    <fields>;
}

# Keep serializable classes structure
-keep,allowobfuscation,allowshrinking @kotlinx.serialization.Serializable class * {
    static **.*$Companion Companion;
}

# ==================== CORE DATABASE CLASSES ====================
# Keep cache metadata classes
-keep class com.mustafakocer.core_database.cache.** { *; }

# Keep pagination classes
-keep class com.mustafakocer.core_database.pagination.** { *; }

# Keep DAO interfaces
-keep interface com.mustafakocer.core_database.dao.** { *; }
-keep interface com.mustafakocer.core_database.pagination.dao.** { *; }

# Keep converters
-keep class com.mustafakocer.core_database.converters.** { *; }

# ==================== PAGING 3 ====================
# Keep Paging 3 classes
-keep class androidx.paging.** { *; }
-dontwarn androidx.paging.**

# ==================== HILT/DAGGER ====================
# Keep Hilt generated classes
-keep class * extends dagger.hilt.internal.GeneratedComponent
-keep class **.*_HiltModules { *; }
-keep class **.*_HiltModules$* { *; }

# ==================== COROUTINES ====================
# Keep coroutines classes
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**