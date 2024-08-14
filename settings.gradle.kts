pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Picture2PC"

val usingAndroidStudio =
    (System.getProperty("idea.paths.selector") ?: "").startsWith("AndroidStudio")

include(":desktop")
include(":common")
if (true) {
    include(":android")
}
else {
    println("Android development only available in Android Studio")
}

