// feature-movies/build.gradle.kts
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("androidx.navigation.safeargs.kotlin")
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
        buildConfig = true
    }
}

dependencies {
// ⭐ CORE DEPENDENCIES (Everything inherited through api)
    implementation(project(":core-ui"))        // → Compose, Material3, Coil, core-common
    implementation(project(":core-network"))   // → Retrofit, Serialization, core-common
    implementation(project(":core-database"))  // → Room, Paging, core-common
    implementation(project(":core-preferences")) // ✅ NEW: Theme & Preferences
    implementation(project(":navigation-contracts")) // <-- SÖZLEŞMELERİ BİLMELİ
    implementation(project(":core-android")) //

    // Navigation modülü
    implementation(libs.androidx.navigation.compose) // <-- NavGraphBuilder İÇİN GEREKLİ
    implementation(libs.hilt.navigation.compose) // <-- Tip-güvenli rotalar için

    // 📄 PAGING 3 COMPOSE - ✅ EKLENDİ
    implementation(libs.paging.compose)

    // coil need this
    implementation(libs.coil.network.okhttp)

    // 📱 COMPOSE LIFECYCLE (Feature-specific)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // 💉 HILT PROCESSING (Required for this module's @Inject annotations)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android) // hilt pluginini eklediğimiz için plugin doğrudan iletişime geçiyor, burada olmak zorunda

    // navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // 🎨 APP-SPECIFIC UI
    implementation(libs.androidx.activity.compose)

    // GSON
    api(libs.gson)

    // 📊 TESTING
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    // 📱 UI TESTING
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // 🛠️ DEBUG
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}