pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
}

rootProject.name = "Picture2PC"

val usingAndroidStudio =
    (System.getProperty("idea.paths.selector") ?: "").startsWith("AndroidStudio")

include(":desktop")
include(":common")

if (true /*usingAndroidStudio*/) {
    include(":android")
}
else {
    println("Android development only available in Android Studio")
}

