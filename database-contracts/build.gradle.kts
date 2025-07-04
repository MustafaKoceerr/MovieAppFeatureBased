plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

}

android {
    namespace = "com.mustafakocer.database_contracts"
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
    // ⭐ SADECE GEREKLI MINIMUMLAR

    // Room - PagingSource return type için
    api(libs.room.runtime)
    api(libs.paging.runtime)

    // core-database - RemoteKeyDao reference için
    api(project(":core-database"))

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ❌ NO HILT - contracts'ta DI gerekmez
    // ❌ NO SERIALIZATION - contracts'ta gerekmez
    // ❌ NO OTHER MODULES
}