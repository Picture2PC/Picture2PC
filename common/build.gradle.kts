plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.multiplatform)
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
}
