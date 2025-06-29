// core-database/build.gradle.kts

/**
 * TEACHING MOMENT: Core Database Module Dependencies
 *
 * REUSABLE DATABASE MODULE:
 * ✅ Generic Room infrastructure
 * ✅ Pagination 3 integration
 * ✅ Type-safe implementations
 * ✅ Minimal dependencies
 *
 * DEPENDENCY PRINCIPLES:
 * - Sadece core-common'a depend eder
 * - Feature module'lara depend etmez
 * - Network'e depend etmez (injection ile alır)
 * - UI'a depend etmez
 */

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mustafakocer.core_database"
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

    // 🗄️ ROOM DATABASE (API - çünkü feature modules kullanacak)
    api(libs.room.runtime)
    api(libs.room.ktx)
    ksp(libs.room.compiler)

    // 📄 PAGINATION 3 (API - feature modules kullanacak)
    api(libs.paging.runtime)
    api(libs.paging.compose)

    // 💉 DEPENDENCY INJECTION
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // 🔄 SERIALIZATION (for type converters)
    implementation(libs.kotlinx.serialization.json)

    // ⚡ COROUTINES
    implementation(libs.kotlinx.coroutines.android)

    // 📊 TESTING
    testImplementation(libs.junit)
    testImplementation("androidx.room:room-testing:2.6.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")

    // INSTRUMENTED TESTING
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ⚠️ NO NETWORK DEPENDENCIES - Network injection ile gelir
    // ⚠️ NO UI DEPENDENCIES - Pure database logic
}