pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
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

