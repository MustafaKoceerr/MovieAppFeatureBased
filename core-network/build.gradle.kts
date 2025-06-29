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

    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)

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
    // ⭐ CORE DEPENDENCY (Brings Hilt, Coroutines, Serialization)
    api(project(":core-common"))

    // 🌐 NETWORK SPECIFIC ONLY
    api(libs.retrofit.core)
    api(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.logging)

    // 💉 HILT KSP (Required for annotation processing)
    ksp(libs.hilt.compiler)  // ✅ Still needed for this module's @Inject

    // 📊 TESTING
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // ❌ NO: hilt.android (inherited from core-common)
    // ❌ NO: kotlinx.coroutines.android (inherited)
    // ❌ NO: kotlinx.serialization.json (inherited)

    // ⚠️ NO UI DEPENDENCIES - Pure network logic!
    // ❌ No Compose
    // ❌ No Activity/Fragment
    // ❌ No Navigation
}