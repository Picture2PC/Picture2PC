import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.compose.multiplatform)
}

repositories {
    mavenCentral()
}

group = "com.github.picture2pc.desktop"
version = "${rootProject.version}.0"

kotlin {
    jvm()
    linuxX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                implementation(libs.org.jetbrains.kotlin.kotlin.stdlib)
                implementation(libs.opencv)
                implementation(project(":common"))
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

compose.desktop {
    application {
        mainClass = "com.github.picture2pc.desktop.MainKt"
        jvmArgs += listOf("-Djava.library.path=libs")

        nativeDistributions {
            modules("java.instrument", "java.management", "jdk.unsupported")
            targetFormats(TargetFormat.Exe, TargetFormat.Msi, TargetFormat.Deb)
            packageName = rootProject.name
            packageVersion = version.toString()
            windows {
                shortcut = true
            }
        }
    }
}
