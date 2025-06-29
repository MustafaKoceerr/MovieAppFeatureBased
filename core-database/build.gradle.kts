// core-database/build.gradle.kts

/**
 * TEACHING MOMENT: Core Database Module Dependencies
 *
 * REUSABLE DATABASE MODULE:
 * ✅ Generic Room infrastructure
 * ✅ Pagination 3 integration
 * ✅ Type-safe implementations
 * ✅ Minimal dependencies
 *
 * DEPENDENCY PRINCIPLES:
 * - Sadece core-common'a depend eder
 * - Feature module'lara depend etmez
 * - Network'e depend etmez (injection ile alır)
 * - UI'a depend etmez
 */

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)

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
    // ⭐ CORE DEPENDENCY (Brings Hilt, Coroutines, Serialization)
    api(project(":core-common"))

    // 🗄️ DATABASE SPECIFIC ONLY
    api(libs.room.runtime)
    api(libs.room.ktx)
    ksp(libs.room.compiler)

    // 📄 PAGINATION SPECIFIC
    api(libs.paging.runtime)
    api(libs.paging.compose)

    // 💉 HILT KSP (Required for annotation processing)
    ksp(libs.hilt.compiler)  // ✅ Still needed for this module's @Inject

    // 📊 TESTING
    testImplementation("androidx.room:room-testing:2.6.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ❌ NO: hilt.android (inherited from core-common)
    // ❌ NO: kotlinx.coroutines.android (inherited)
    // ❌ NO: Dependencies on core-network!
    // ⚠️ NO NETWORK DEPENDENCIES - Network injection ile gelir
    // ⚠️ NO UI DEPENDENCIES - Pure database logic
}