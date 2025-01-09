plugins {
    kotlin("multiplatform")
    alias(libs.plugins.compose.multiplatform)
    kotlin("plugin.serialization") version "1.8.20"
}

group = "com.github.picture2pc.common"
version = "${rootProject.version}.0"

kotlin {
    jvm()
    linuxX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.ui)
                api(compose.foundation)
                api(compose.material3)
                api(compose.runtime)

                api(libs.koin.core)
                api(libs.koin.compose)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.cbor)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val linuxX64Main by getting
        val mingwX64Main by getting
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

sourceSets["commonTest"].dependsOn(sourceSets["commonMain"])
