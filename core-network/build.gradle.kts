// core/core-network/build.gradle.kts - REUSABLE NETWORK MODULE

/**
 * TEACHING MOMENT: Reusable Network Module
 *
 * This module is designed to be REUSABLE across projects:
 * ✅ Generic API infrastructure
 * ✅ Standard error handling
 * ✅ Common network utilities
 * ✅ No project-specific dependencies
 *
 * REUSABILITY RULES:
 * - No hardcoded API endpoints
 * - Generic error handling
 * - Configurable base URLs
 * - Standard HTTP client setup
 */
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mustafakocer.core_network"
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
// ⭐ CORE DEPENDENCY - Base infrastructure
    api(project(":core-common"))

    // 🌐 NETWORK CORE
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)

    // 💉 DEPENDENCY INJECTION
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ⚡ COROUTINES
    implementation(libs.kotlinx.coroutines.android)

    // 📊 TESTING
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // ⚠️ NO UI DEPENDENCIES - Pure network logic!
    // ❌ No Compose
    // ❌ No Activity/Fragment
    // ❌ No Navigation
}