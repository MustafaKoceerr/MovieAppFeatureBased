// navigation-contracts/build.gradle.kts

/**
 * NAVIGATION CONTRACTS MODULE
 *
 * PURPOSE:
 * ✅ App-specific navigation interfaces and destinations
 * ✅ Centralized navigation contracts for all features
 * ✅ Type-safe navigation with @Serializable
 * ✅ No UI dependencies - pure contracts
 *
 * ARCHITECTURE PLACEMENT:
 * - Features depend on this module for navigation contracts
 * - App module implements these contracts
 * - No circular dependencies
 */

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mustafakocer.navigation_contracts"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // ⭐ NO DEPENDENCIES ON OTHER MODULES
    // This module should be dependency-free to avoid circular dependencies

    // 🔗 SERIALIZATION - For type-safe navigation
    implementation(libs.kotlinx.serialization.json)


    // 📊 TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ⚠️ NO UI DEPENDENCIES
    // ❌ No Compose
    // ❌ No Navigation Compose
    // ❌ No other feature modules
    // ❌ No core modules (to avoid circular deps)
}