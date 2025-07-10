plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mustafakocer.core_preferences"
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
    // ⭐ MINIMAL DEPENDENCIES - App agnostic!

    // 📱 CORE ANDROID
    api(libs.androidx.core.ktx)

    // 📦 DATASTORE - Core infrastructure
    api(libs.androidx.datastore.preferences)

    // 💉 HILT - For providing DataStore
    api(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // 📊 TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ❌ NO UI DEPENDENCIES
    // ❌ NO BUSINESS LOGIC DEPENDENCIES
    // ❌ NO FEATURE DEPENDENCIES
    // ✅ Pure infrastructure only!
}