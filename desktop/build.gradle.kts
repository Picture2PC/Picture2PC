import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.github.picture2pc.desktop"
version = "${rootProject.version}.0"

dependencies {
    implementation(compose.desktop.common)
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "com.github.picture2pc.desktop.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.AppImage)
        }
    }
}
