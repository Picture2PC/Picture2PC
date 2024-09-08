import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.multiplatform)
}

repositories {
    mavenCentral()
}

group = "com.github.picture2pc.desktop"
version = "${rootProject.version}.0"

dependencies {
    implementation(compose.desktop.common)
    implementation(compose.desktop.currentOs)
    implementation(libs.org.jetbrains.kotlin.kotlin.stdlib)
    implementation(libs.opencv.v4550)
    implementation(project(":common"))
}

compose.desktop {
    application {
        mainClass = "com.github.picture2pc.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.AppImage)
            packageName = "Picture2PC"
            packageVersion = "0.0.1"
        }
    }
}