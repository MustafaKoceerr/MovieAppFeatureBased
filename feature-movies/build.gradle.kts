// feature-movies/build.gradle.kts
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
        buildConfig = true
    }
}

dependencies {
    // --- CORE MODÜL BAĞIMLILIKLARI ---
    implementation(project(":core-ui"))
    implementation(project(":core-network"))
    implementation(project(":core-database"))
    implementation(project(":core-preferences"))
    implementation(project(":core-android"))
    implementation(project(":core-domain"))
    implementation(project(":navigation-contracts"))

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // --- HILT ---
    // Bu modül @HiltViewModel, @Inject, @Module kullandığı için zorunlu.
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // --- COMPOSE & UI ---
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // --- PAGING 3 ---
    // :core-database'den 'api' ile geldiği için bu satıra gerek kalmaz.
    // Room
    implementation(libs.room.ktx)
    // Paging collectAsLazyPagingItems compose
    implementation(libs.paging.compose)

    // --- GÖRSELLEŞTİRME ---
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.compose)

    // --- VERİ DÖNÜŞÜMÜ ---
    // MovieConverters'da kullanılıyor.
    implementation(libs.gson)

    // --- TEST ---
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}