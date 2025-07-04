plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mustafakocer.database_contracts"
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

    // 🔗 SERIALIZATION - For contracts only
    implementation(libs.kotlinx.serialization.json)

    // 🗄️ ROOM - For DAO contracts only (no implementation)
    api(libs.room.runtime)  // For PagingSource return types
    api(libs.paging.runtime) // For PagingSource contracts

    // 💉 HILT - For DI annotations
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // 📊 TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ⚠️ NO DEPENDENCIES ON OTHER PROJECT MODULES
    // ❌ No core-database (to avoid circular deps)
    // ❌ No feature modules
    // ❌ No UI dependencies
}