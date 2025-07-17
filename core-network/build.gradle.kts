// core/core-network/build.gradle.kts - REUSABLE NETWORK MODULE

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
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
    api(project(":core-domain"))

    // LanguageProvider'a erişim için.
    implementation(project(":core-preferences"))

    // --- HILT ---
    // Bu modül @Inject, @Module, @Singleton kullandığı için Hilt'e ihtiyacı var.
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // --- AĞ KÜTÜPHANELERİ ---
    api(libs.retrofit.core)
    api(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp.logging)

    // --- TEST ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}