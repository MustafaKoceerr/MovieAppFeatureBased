// core-ui/build.gradle.kts

/**
 * TEACHING MOMENT: Pure Core-UI Module
 *
 * PRINCIPLE: Core-UI should be PURE UI infrastructure
 * ✅ Reusable Compose components
 * ✅ Theme, Colors, Typography
 * ✅ Image loading, Icons
 * ❌ NO business logic dependencies
 * ❌ NO Hilt, ViewModel, Navigation, Coroutines
 */
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.kotlin.compose)

}

android {
    namespace = "com.mustafakocer.core_ui"
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
// ⭐ CORE DEPENDENCY - UiContract types için
    api(project(":core-common"))

    // 🎨 COMPOSE CORE (Pure UI)
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.ui)
    api(libs.androidx.ui.graphics)
    api(libs.androidx.ui.tooling.preview)
    api(libs.androidx.material3)

    // 🎭 MATERIAL ICONS EXTENDED
    // Eğer extended icon'lara core-ui kütüphanesini implement edeceğin feature'larda gerçekten ihtiyacın varsa
    // core-ui modülüne extended kütüphanesini ekle, ancak her feature'da extended icons'lara ihtiyacın yoksa ve core icons'lar yeterliyse
    // bu durumda core-ui modülüne extended kütüphanesini implement ETME!
    api(libs.androidx.material.icons.extended)

    // 🖼️ IMAGE LOADING
    api(libs.coil.compose)
    implementation(project(":core-preferences"))
//    api(libs.coil.network.okhttp) // gradle hata veriyor.

    // 📊 TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // 🛠️ DEBUG TOOLS
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}