// core-database/build.gradle.kts

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.ksp)
}

android {
    namespace = "com.mustafakocer.core_database"
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
    api(project(":core-domain"))

    // --- VERİTABANI KÜTÜPHANELERİ ---
    api(libs.room.runtime)
    api(libs.room.ktx)
    ksp(libs.room.compiler) // Sadece bu modülün kendi @Dao/@Entity'lerini derlemesi için.

    // --- PAGING 3 KÜTÜPHANELERİ ---

    api(libs.paging.runtime)
    api(libs.room.paging)
    // Bu, UI katmanına ait bir bağımlılık gibi görünse de, PagingSource'un
    // kendisi genellikle UI'a kadar taşındığı için burada 'api' olarak tutmak pratiktir.
    api(libs.paging.compose)

    // --- TEST ---
    testImplementation(libs.junit)
    testImplementation(libs.testng)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}