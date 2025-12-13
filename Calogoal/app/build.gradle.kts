plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.calogoal"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.calogoal"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // READ THE KEYS FROM gradle.properties AND EXPOSE THEM TO THE CODE
        buildConfigField("String", "EDAMAM_APP_ID", project.property("EDAMAM_APP_ID") as String)
        buildConfigField("String", "EDAMAM_APP_KEY", project.property("EDAMAM_APP_KEY") as String)
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.navigation:navigation-compose:2.9.6")
    implementation(libs.androidx.room.ktx)
    implementation("androidx.compose.material:material-icons-extended:1.6.0")
    implementation(libs.androidx.compose.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-perf")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-messaging")

    // Hilt Core Dependencies
    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-compiler:2.57.2")

    // Hilt for Jetpack Compose (For using hiltViewModel())
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Networking - Retrofit and Moshi for JSON parsing
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // OkHttp Logging Interceptor for debugging network requests
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    implementation("androidx.room:room-ktx:${room_version}")
}