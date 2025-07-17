// core-preferences gradle
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mustafakocer.core_preferences"
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

     implementation(project(":core-domain"))

    // --- HILT ---
    // Bu modül @Inject, @Module, @Singleton kullandığı için Hilt'e ihtiyacı var.
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // --- DATASTORE ---
    api(libs.androidx.datastore.preferences)

    // --- COROUTINES ---
    // Repository'lerin Flow döndürmesi ve LanguageProvider'ın CoroutineScope kullanması için gerekli.
    implementation(libs.kotlinx.coroutines.core)


    // --- TEST ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}