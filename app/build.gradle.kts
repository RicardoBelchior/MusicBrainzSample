plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
}

android {
    namespace = "com.rbelchior.dicetask"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.rbelchior.dicetask"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.compose.ui.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3.material3)

    implementation(libs.mdc)
    implementation(libs.navigation.compose)

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
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation(libs.androidx.test.espressoCore)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}
