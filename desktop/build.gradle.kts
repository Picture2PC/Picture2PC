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
    implementation(libs.opencv)

    implementation(project(":common"))
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