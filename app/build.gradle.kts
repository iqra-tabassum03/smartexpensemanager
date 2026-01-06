plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.smartexpensemanager"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.smartexpensemanager"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // --- Version Variables ---
    val roomVersion = "2.8.4"
    val lifecycleVersion = "2.10.0"
    val coroutinesVersion = "1.10.2"
    val materialVersion = "1.13.0"
    val fragmentVersion = "1.8.9"
    val navVersion = "2.9.6"

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    // Core Android & UI
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")

    // Fragment KTX
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    // Room Database
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Jetpack Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
