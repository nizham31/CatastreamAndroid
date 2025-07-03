plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.belajar.catastreamandroidapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.belajar.catastreamandroidapp"
        minSdk = 26 // saran minSdk: 24 supaya support Compose, bisa naik 30 kalau target device baru saja
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["auth0Scheme"] = "catastream"
        manifestPlaceholders["auth0Domain"] = "dev-l6v4knjejv1w2y3q.us.auth0.com"
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9" // sesuaikan dengan compose-bom versi 2024.05.00
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Jetpack Compose (pakai BOM supaya gampang update)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.tooling.preview)
    implementation(libs.compose.activity)

    // Untuk efek shimmer yang lebih baik
    implementation("com.facebook.shimmer:shimmer:0.5.0")
// Dan wrapper-nya untuk Jetpack Compose
    implementation("com.valentinilk.shimmer:compose-shimmer:1.2.0")


    implementation("androidx.compose.material:material-icons-core-android:1.6.7") // Anda mungkin sudah punya ini
    implementation("androidx.compose.material:material-icons-extended-android:1.6.7") // <-- TAMBAHKAN BARIS INI
    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation("com.auth0.android:auth0:2.+")         // SDK Auth0
    implementation("androidx.browser:browser:1.7.0")         // Untuk Web Auth

    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")

}