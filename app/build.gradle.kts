plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.project.weathersnap"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.project.weathersnap"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7") //

// Hilt for DI
    implementation("com.google.dagger:hilt-android:2.51") //
    ksp("com.google.dagger:hilt-compiler:2.51") //
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // [cite: 11, 12]

// Retrofit & Network
    implementation("com.squareup.retrofit2:retrofit:2.11.0") // [cite: 13]
    implementation("com.squareup.retrofit2:converter-gson:2.11.0") // [cite: 14]
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // [cite: 15]

// Room Database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version") //
    implementation("androidx.room:room-ktx:$room_version") //
    ksp("androidx.room:room-compiler:$room_version") //

// CameraX
    val camerax_version = "1.3.3"
    implementation("androidx.camera:camera-core:${camerax_version}") // [cite: 17]
    implementation("androidx.camera:camera-camera2:${camerax_version}") // [cite: 17]
    implementation("androidx.camera:camera-lifecycle:${camerax_version}") // [cite: 17]
    implementation("androidx.camera:camera-view:${camerax_version}") // [cite: 17]
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}