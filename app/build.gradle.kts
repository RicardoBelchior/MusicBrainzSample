plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version libs.versions.kotlin.get()
    id("com.google.devtools.ksp") version libs.versions.ksp
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
}

android {
    namespace = "com.rbelchior.dicetask"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rbelchior.dicetask"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.compose.ui.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3.material3)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.mdc)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)

    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.datetime)

    implementation(libs.ktor.android)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.negotiation)
    implementation(libs.ktor.json)

    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    implementation(libs.logcat)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.kotlin.coroutines.android)
    testImplementation(libs.ktor.mock)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espressoCore)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.kotest.assertions)
    androidTestImplementation(libs.turbine)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
