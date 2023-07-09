import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
}

group = "com.github.picture2pc"
version = "0.1"

subprojects {
    val subprojectName = name

    repositories {
        mavenCentral()
    }

    afterEvaluate {

        kotlinExtension.apply {

            jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(8))
            }

            tasks.withType<KotlinCompile<KotlinJvmOptions>> {
                kotlinOptions {
                    jvmTarget = "1.8"
                }
            }

            sourceSets["main"].apply {
                dependencies {
                    if (subprojectName != "common") {
                        implementation(project(":common"))
                    }
                }
            }
        }

    }
}
