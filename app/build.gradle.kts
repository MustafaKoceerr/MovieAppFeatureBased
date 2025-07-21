// app module gradle.
import java.util.Properties

// Dosyanın en üstüne bu bloğu ekliyoruz.
// Bu blok, projenin kök dizinindeki local.properties dosyasını bulur.
val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
localProperties.load(localPropertiesFile.inputStream())

// local.properties'den API_KEY değerini okur.
val apiKey = localProperties.getProperty("API_KEY")

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

            // ✅ EKSİK FIELD'LAR EKLENDI
            buildConfigField("String", "API_URL", "\"https://api.themoviedb.org/3/\"")
            buildConfigField("String", "API_KEY", "$apiKey")
            buildConfigField("String", "APP_NAME", "\"Movie App\"")
            buildConfigField("Boolean", "ENABLE_LOGGING", "false")
        }

        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
            isMinifyEnabled = false

            // ✅ EKSİK FIELD'LAR EKLENDI
            buildConfigField("String", "API_URL", "\"https://api.themoviedb.org/3/\"")
            buildConfigField("String", "API_KEY", "$apiKey")
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
}

dependencies {
    implementation(project(":core-ui"))
    implementation(project(":core-android"))
    implementation(project(":core-network"))
    implementation(project(":core-database"))
    implementation(project(":core-preferences"))
    implementation(project(":core-domain"))

    // :app modülü, tüm feature ve core modüllerini bir araya getiren
    // en üst katman olduğu için, hepsine 'implementation' ile bağımlı olması normaldir.
    implementation(project(":feature-movies"))
    implementation(project(":feature-auth"))
    implementation(project(":feature-splash"))
    implementation(project(":navigation-contracts"))

    // HILT
    // :app modülü @AndroidEntryPoint ve @HiltAndroidApp kullandığı için zorunlu.
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // ANDROIDX & UI
    // MainActivity ve temel UI işlemleri için.
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat) // attachBaseContext ve AppCompatDelegate için.

    // NAVIGATION
    // NavHost ve NavController için.
    implementation(libs.androidx.navigation.compose)

    // ROOM
    // AppDatabase'i derleyebilmek için KSP burada da gerekli.
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // GSON
    implementation(libs.gson)

    // --- TEST ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // --- DEBUG ---
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}