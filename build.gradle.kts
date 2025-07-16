
//# gradle/libs.versions.toml - CENTRALIZED DEPENDENCY MANAGEMENT

//# Root build.gradle.kts
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false

    alias(libs.plugins.androidx.navigation.safe.args) apply false // ✅ BURAYI EKLE
}