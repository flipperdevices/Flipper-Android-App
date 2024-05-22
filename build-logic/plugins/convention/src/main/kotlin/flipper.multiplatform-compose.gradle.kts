import com.android.build.gradle.BaseExtension
import gradle.kotlin.dsl.accessors._089327967f8e17bf02e7a1b7ad8a66b0.sourceSets

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

configure<BaseExtension> {
    commonAndroid(project)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    jvm("desktop")

    sourceSets {
        val jvmMain by creating

        val androidMain by getting {
            dependsOn(jvmMain)
        }
        val desktopMain by getting {
            dependsOn(jvmMain)
        }

        androidMain.dependencies {
            implementation(libs.compose.tooling)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}
