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
    // ✅ YENİ: Product Flavors
    flavorDimensions += "version"
    productFlavors {
        create("free") {
            dimension = "version"
            applicationIdSuffix = ".free"
            versionNameSuffix = "-free"

            // Free version BuildConfig
            buildConfigField("Boolean", "IS_PREMIUM", "false")
            buildConfigField("String", "VERSION_TYPE", "\"Free\"")
            buildConfigField("Boolean", "ENABLE_ADS", "true")
            buildConfigField("Boolean", "ENABLE_OFFLINE_MODE", "false")

            // Free version app name
            resValue("string", "app_name", "Movie App Free")
        }

        create("premium") {
            dimension = "version"
            applicationIdSuffix = ".premium"
            versionNameSuffix = "-premium"

            // Premium version BuildConfig
            buildConfigField("Boolean", "IS_PREMIUM", "true")
            buildConfigField("String", "VERSION_TYPE", "\"Premium\"")
            buildConfigField("Boolean", "ENABLE_ADS", "false")
            buildConfigField("Boolean", "ENABLE_OFFLINE_MODE", "true")

            // Premium version app name
            resValue("string", "app_name", "Movie App Premium")
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
        buildConfig = true  // ✅ BuildConfig enable
    }
}

dependencies {
// ⭐ FEATURE MODULES (All dependencies inherited through these)
    implementation(project(":di"))
    implementation(project(":navigation-contracts"))  // ✅ NEW
    implementation(project(":core-common"))
    implementation(project(":core-ui"))
    implementation(project(":data-common"))
    implementation(project(":feature-movies"))

    // 🗄️ ROOM DATABASE - ✅ EKLENDİ (App level'da KSP için gerekli)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler) // ✅ ÖNEMLİ: Room annotation processor

    // navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // 🎨 APP-SPECIFIC UI
    implementation(libs.androidx.activity.compose)

    // Navigation modülü
    implementation(libs.androidx.navigation.compose)

    // 💉 HILT KSP (For app's @Inject annotations)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android) // hilt pluginini eklediğimiz için plugin doğrudan iletişime geçiyor, burada olmak zorunda
    // core-common'dan katılım almak bu durumda yetmiyor.
    // serialization plugininin implementasyon istememes, hilt-serialization pluginlerinin beklenti farkları olmasından kaynaklanır.

    // 📊 TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // 🛠️ DEBUG
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // 🌀 COROUTINES
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
}