import com.android.build.gradle.BaseExtension

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
        /**
         * We shouldn't create sources, which is named as original targets sourcesets.
         *
         * As an example - jvm() target will create sourceSet jvmMain - and we would have conflicts
         * with our create jvmMain sourceSet
         *
         * This is the reason to name it `sharedJvmMain`
         */
        val sharedJvmMain by creating

        val androidMain by getting {
            dependsOn(sharedJvmMain)
        }
        val desktopMain by getting {
            dependsOn(sharedJvmMain)
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
