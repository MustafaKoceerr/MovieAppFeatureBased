// app module gradle.
import java.util.Properties

// local.properties'ten API_KEY okunur. Dosya veya anahtar yoksa boş string kullanılır, build kırılmaz.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
val apiKey: String = localProperties.getProperty("API_KEY")
    ?.trim()?.removeSurrounding("\"")
    .orEmpty()
if (apiKey.isEmpty()) {
    logger.warn("API_KEY local.properties içinde bulunamadı; TMDB istekleri başarısız olacak.")
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mustafakocer.movieappfeaturebasedclean"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mustafakocer.movieappfeaturebasedclean"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_URL", "\"https://api.themoviedb.org/3/\"")
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "APP_NAME", "\"Movie App\"")
            buildConfigField("Boolean", "ENABLE_LOGGING", "false")
        }

        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
            isMinifyEnabled = false

            buildConfigField("String", "APP_NAME", "\"Movie App Debug\"")
            buildConfigField("Boolean", "ENABLE_LOGGING", "true")
        }
    }
//    // ✅ YENİ: Product Flavors
//    flavorDimensions += "version"
//    productFlavors {
//        create("free") {
//            dimension = "version"
//            applicationIdSuffix = ".free"
//            versionNameSuffix = "-free"
//
//            // Free version BuildConfig
//            buildConfigField("Boolean", "IS_PREMIUM", "false")
//            buildConfigField("String", "VERSION_TYPE", "\"Free\"")
//            buildConfigField("Boolean", "ENABLE_ADS", "true")
//            buildConfigField("Boolean", "ENABLE_OFFLINE_MODE", "false")
//
//            // Free version app name
//            resValue("string", "app_name", "Movie App Free")
//        }
//
//        create("premium") {
//            dimension = "version"
//            applicationIdSuffix = ".premium"
//            versionNameSuffix = "-premium"
//
//            // Premium version BuildConfig
//            buildConfigField("Boolean", "IS_PREMIUM", "true")
//            buildConfigField("String", "VERSION_TYPE", "\"Premium\"")
//            buildConfigField("Boolean", "ENABLE_ADS", "false")
//            buildConfigField("Boolean", "ENABLE_OFFLINE_MODE", "true")
//
//            // Premium version app name
//            resValue("string", "app_name", "Movie App Premium")
//        }
//    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true  // ✅ BuildConfig enable
    }
    testOptions {
        unitTests {
            // android.util.Log etc. return defaults instead of throwing in JVM unit tests.
            isReturnDefaultValues = true
            // Compose UI tests (Robolectric) need the app resources (strings, drawables).
            isIncludeAndroidResources = true
            // Fixed English locale: makes string assertions deterministic and avoids the Turkish-locale
            // "WINDOWS".lowercase() -> "wındows" bug that stops Robolectric's native libs from loading.
            all { it.jvmArgs("-Duser.language=en", "-Duser.country=US") }
        }
    }
}

dependencies {
    // Core Modüller
    implementation(project(":core-ui"))
    implementation(project(":core-data"))

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // AndroidX & UI (Sadece 'app' modülünün ihtiyaç duydukları)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat) // Genellikle Activity temaları için hala gereklidir
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // ROOM
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Paging (collectAsLazyPagingItems)
    implementation(libs.paging.compose)

    // Görsel yükleme
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.compose)

    // --- TEST ---
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.paging.testing)
    testImplementation(libs.robolectric)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // --- DEBUG ---
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}