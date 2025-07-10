
# Core Preferences Module Consumer ProGuard Rules

# ==================== DATASTORE PREFERENCES ====================
# Keep DataStore related classes
-keep class androidx.datastore.** { *; }
-dontwarn androidx.datastore.**

# ==================== PREFERENCES MODELS ====================
# Keep preference enums and models
-keep enum com.mustafakocer.core_preferences.models.** { *; }

# ==================== HILT ====================
# Keep Hilt generated classes for this module
-keep class com.mustafakocer.core_preferences.di.** { *; }