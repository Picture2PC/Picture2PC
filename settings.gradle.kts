pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        val kotlinVersion = "1.8.20"
        val androidGradleVersion = "8.0.2"
        val composeDesktopVersion = "1.4.0"

        kotlin("jvm") version kotlinVersion
        id("org.jetbrains.compose") version composeDesktopVersion
        kotlin("android") version kotlinVersion
        id("com.android.application") version androidGradleVersion
    }
}

rootProject.name = "Picture2Pc"

val usingAndroidStudio = System.getProperty("idea.platform.prefix") == "AndroidStudio"

include(":desktop")
include(":common")
if (usingAndroidStudio) {
    include(":android")
}
else {
    println("Android development only available in Android Studio")
}

