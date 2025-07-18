// navigation-contracts/build.gradle.kts

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.mustafakocer.navigation_contracts"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
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

    // Test kaynakları olmadığı için bu bloğu kaldırabiliriz veya boş bırakabiliriz.
    // Gradle'ın kafasının karışmaması için boş bırakmak daha güvenli olabilir.
    sourceSets {
        named("test") { java.srcDirs() }
        named("androidTest") { java.srcDirs() }
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
    api(libs.kotlinx.serialization.json)
}