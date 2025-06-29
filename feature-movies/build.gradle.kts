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
// ⭐ CORE DEPENDENCIES - Inherited chain
    api(project(":core-common"))      // ✅ Base infrastructure
    api(project(":core-network"))     // ✅ Network + core-common
    api(project(":core-database"))    // ✅ Database + core-common

    // 🎨 COMPOSE UI (Feature-specific)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // 🧭 NAVIGATION (Feature-specific)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // 💉 HILT KSP (Required for annotation processing)
    ksp(libs.hilt.compiler)  // ✅ Still needed for this module's @Inject
    implementation(libs.hilt.android) // hilt pluginini eklediğimiz için plugin doğrudan iletişime geçiyor, burada olmak zorunda

    // 🖼️ IMAGE LOADING (Feature-specific)
    implementation(libs.coil.compose)

    // ⚡ INHERITED from core modules:
    // ❌ hilt.android (from core-common)
    // ❌ kotlinx.coroutines.android (from core-common)
    // ❌ kotlinx.serialization.json (from core-common)
    // ❌ retrofit.core (from core-network)
    // ❌ room.runtime (from core-database)
    // ❌ paging.runtime (from core-database)

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