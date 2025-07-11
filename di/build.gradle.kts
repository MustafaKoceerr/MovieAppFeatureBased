plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mustafakocer.di"
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
// ⭐ FEATURE MODULES (All dependencies inherited through these)
    implementation(project(":core-common"))
    implementation(project(":core-ui"))
    implementation(project(":core-network"))
    implementation(project(":feature-movies"))
    implementation(project(":core-database"))
    implementation(project(":core-preferences"))
    implementation(project(":data-common"))
    implementation(project(":core-database-contract"))

    // 🗄️ ROOM DATABASE - ✅ EKLENDİ (App level'da KSP için gerekli)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler) // ✅ ÖNEMLİ: Room annotation processor

    // 💉 HILT KSP (For app's @Inject annotations)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android) // hilt pluginini eklediğimiz için plugin doğrudan iletişime geçiyor, burada olmak zorunda

    // DataStore Preferences (YENİ)
    api(libs.androidx.datastore.preferences)
    // App modülünde kullanılacağı için api ile implement edildiği yere de açılmasını sağladım.
    // Böylece bu modülü implement eden herhangi bir modül ayrıca preferences'ı implement etmek zorunda kalmayacak.

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.testng)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}