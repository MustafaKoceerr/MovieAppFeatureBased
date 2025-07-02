// feature-movies/build.gradle.kts
/**
 * TEACHING MOMENT: Feature Module Dependencies
 *
 * DEPENDENCY HIERARCHY:
 * feature-movies → core-network → core-common
 * feature-movies → core-ui (for shared components)
 * feature-movies → core-database (for pagination)
 *
 * ✅ Feature modules depend on CORE modules
 * ❌ Feature modules NEVER depend on other features
 * ❌ Core modules NEVER depend on features
 */

// En üste ekle
import java.util.Properties

// build.gradle.kts (module-level)'in en üstüne ekle:
val localProperties = File(rootDir, "local.properties")
val apiKey = Properties().apply {
    load(localProperties.inputStream())
}.getProperty("API_KEY") ?: throw GradleException("API_KEY not found in local.properties")

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
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


        // API key'i BuildConfig'e ekliyoruz
        buildConfigField("String", "API_KEY", apiKey)
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
        buildConfig = true
    }
}

dependencies {
    // ⭐ CORE DEPENDENCIES (Everything inherited through api)
    implementation(project(":core-ui"))        // → Compose, Material3, Coil, core-common
    implementation(project(":core-network"))   // → Retrofit, Serialization, core-common
    implementation(project(":core-database"))  // → Room, Paging, core-common
    implementation(project(":navigation-contracts"))  // → Room, Paging, core-common

    // coil need this
    implementation(libs.coil.network.okhttp)

    // 📱 COMPOSE LIFECYCLE (Feature-specific)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // 🧭 NAVIGATION (Feature-specific)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // 💉 HILT PROCESSING (Required for this module's @Inject annotations)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android) // hilt pluginini eklediğimiz için plugin doğrudan iletişime geçiyor, burada olmak zorunda

    // navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // 🎨 APP-SPECIFIC UI
    implementation(libs.androidx.activity.compose)
    // 🚫 REMOVED - Already inherited from core modules:
    // ❌ androidx.compose.bom (from core-ui)
    // ❌ androidx.ui.* (from core-ui)
    // ❌ androidx.material3 (from core-ui)
    // ❌ coil.compose (from core-ui)
    // ❌ hilt.android (from core-common)
    // ❌ kotlinx.coroutines.* (from core-common)
    // ❌ kotlinx.serialization.* (from core-network)
    // ❌ retrofit.* (from core-network)
    // ❌ room.* (from core-database)
    // ❌ paging.* (from core-database)

    // 📊 TESTING
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")

    // 📱 UI TESTING
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // 🛠️ DEBUG
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}