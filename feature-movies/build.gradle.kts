/**
 * TEACHING MOMENT: Feature Module Dependencies
 *
 * DEPENDENCY HIERARCHY:
 * feature-movies → core-network → core-common
 * feature-movies → core-ui (for shared components)
 *
 * ✅ Feature modules depend on CORE modules
 * ❌ Feature modules NEVER depend on other features
 * ❌ Core modules NEVER depend on features
 */
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mustafakocer.feature_movies"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    // ⭐ CORE DEPENDENCIES
    implementation(project(":core-common"))
    implementation(project(":core-network"))
    implementation(project(":core-ui"))

    // 🎨 COMPOSE UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // 🧭 NAVIGATION
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // 💉 DEPENDENCY INJECTION
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // 🌐 NETWORK (inherited from core-network)
    // Retrofit, OkHttp, Serialization already available

    // 🖼️ IMAGE LOADING
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    // ⚡ COROUTINES
    implementation(libs.kotlinx.coroutines.android)

    // 📊 TESTING
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")

    // UI TESTING
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // DEBUG
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}