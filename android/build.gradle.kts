plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

group = "com.github.picture2pc.android"
version = "${rootProject.version}.0"

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.koin.android)

    implementation(libs.core.ktx)

    val composeBom = platform("androidx.compose:compose-bom:2023.04.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
    implementation(libs.activity.compose)
    implementation(libs.datastore.preferences)

    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
}

android {
    namespace = "com.github.picture2pc.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.github.picture2pc.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        getByName("release") { }
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
        kotlinCompilerExtensionVersion = "1.4.6"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
