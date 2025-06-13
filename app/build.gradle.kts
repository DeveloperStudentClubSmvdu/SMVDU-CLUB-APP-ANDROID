
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    kotlin("plugin.serialization")
    id ("kotlin-kapt")

}


android {
    namespace = "com.akash.smvduclubapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.akash.smvduclubapp"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load secrets properties file
        val secretsFile = rootProject.file("secrets.properties")
        val secrets = Properties()
        if (secretsFile.exists()) {
            secrets.load(FileInputStream(secretsFile))
        }

        // Add Supabase credentials to BuildConfig
        buildConfigField("String", "SUPABASE_URL", "\"${secrets.getProperty("SUPABASE_URL", "")}\"")
        buildConfigField("String", "SUPABASE_KEY", "\"${secrets.getProperty("SUPABASE_KEY", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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

    implementation(libs.transport.api)
    implementation(libs.transport.api)
    implementation(libs.transport.api)
    val room = "2.6.0"
  implementation ("androidx.room:room-runtime:$room")
    implementation ("androidx.room:room-ktx:$room")
    implementation ("androidx.room:room-paging:$room")
    implementation ("androidx.room:room-rxjava3:$room")
    kapt ("androidx.room:room-compiler:$room")

    implementation ("org.slf4j:slf4j-simple:1.7.36")

//
  implementation("androidx.paging:paging-compose:3.3.5")
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")
    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation ("androidx.compose.material:material-icons-extended:1.5.2")

    implementation("com.google.firebase:firebase-messaging")

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")

    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.2")
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation ("com.github.yalantis:ucrop:2.2.8")

    // Network Image
    implementation(libs.coil.compose)

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.realtime.kt)
    implementation(libs.postgrest.kt)
    implementation(libs.ktor.client.android)
    implementation(libs.kotlinx.serialization.json)
    implementation("io.github.jan-tennert.supabase:gotrue-kt:1.3.2") // Latest as of now
    implementation("io.github.jan-tennert.supabase:supabase-kt:1.3.2")
    implementation("io.github.jan-tennert.supabase:postgrest-kt:1.3.2")
    implementation("io.github.jan-tennert.supabase:storage-kt:1.3.2")
    implementation("io.github.jan-tennert.supabase:functions-kt:1.3.2")
    implementation("io.ktor:ktor-client-cio:2.3.6")

    implementation("io.ktor:ktor-client-okhttp:2.3.6")

    // Kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.7")


    implementation("io.github.jan-tennert.supabase:realtime-kt:1.2.0")

    // Firebase Realtime Database
    implementation ("com.google.firebase:firebase-database-ktx:20.3.0")


    // KTX extensions for coroutines with Firebase
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}