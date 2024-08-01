plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.multiplatform)
    kotlin("plugin.serialization") version "1.8.20"
}

group = "com.github.picture2pc.common"
version = "${rootProject.version}.0"

dependencies {
    api(compose.ui)
    api(compose.foundation)
    api(compose.material3)
    api(compose.runtime)

    api(libs.koin.core)
    api(libs.koin.compose)
    implementation(libs.kotlinx.serialization.json)
}

sourceSets {
    main {
        resources.setSrcDirs(listOf("src/main/resources"))
    }
}