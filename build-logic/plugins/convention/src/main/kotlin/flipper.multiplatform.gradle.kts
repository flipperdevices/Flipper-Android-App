import com.android.build.gradle.BaseExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
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
        val desktopMain by getting

        androidMain.dependencies {
        }
        commonMain.dependencies {
        }
        desktopMain.dependencies {
        }
    }
}
