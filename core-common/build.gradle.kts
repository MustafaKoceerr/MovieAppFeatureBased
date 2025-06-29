// core/core-common/build.gradle.kts - BASE INFRASTRUCTURE MODULE

/**
 * TEACHING MOMENT: core-common Module Purpose
 *
 * Bu module APP'İN FUNDATİON'I:
 * ✅ Shared utilities ve extensions
 * ✅ Common constants
 * ✅ Base exception classes
 * ✅ Core domain models
 *
 * DEPENDENCY RULE: Hiçbir şeye depend etmez, herkes buna depend eder
 */
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mustafakocer.core_common"
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
    // 📱 CORE ANDROID
    api(libs.androidx.core.ktx)  // ✅ Tüm modüller kullanacak

    // 💉 DEPENDENCY INJECTION (Shared)
    api(libs.hilt.android)       // ✅ api - child modules inherit
    ksp(libs.hilt.compiler)      // ✅ Annotation processing

    // ⚡ COROUTINES (Shared)
    api(libs.kotlinx.coroutines.android)  // ✅ api - shared

    // 🔄 SERIALIZATION (Shared)
    api(libs.kotlinx.serialization.json)  // ✅ api - shared

    // 📊 TESTING (Shared)
    testImplementation(libs.junit)
    // NO UI dependencies - core-common is pure logic!
}