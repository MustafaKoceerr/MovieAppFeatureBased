// core-database/build.gradle.kts

/**
 * SADELEŞTIRILMIŞ CORE DATABASE MODULE
 *
 * AMAÇ: Minimal database infrastructure
 * ✅ Room entities ve basic DAO support
 * ✅ Paging 3 config
 * ✅ Cache metadata support
 * ✅ Sıfır business logic - sadece infrastructure
 */

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

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
    // ⭐ CORE DEPENDENCY (Brings Hilt, Coroutines, Serialization)
    api(project(":core-common"))

    // 🗄️ ROOM DATABASE - Core functionality
    api(libs.room.runtime)
    api(libs.room.ktx)
    ksp(libs.room.compiler)

    // 📄 PAGING 3 - Pagination support
    api(libs.paging.runtime)
    api(libs.paging.compose) // ✅ EKLENDİ - Compose için
    api(libs.room.paging)

    // 📊 TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}