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
        }
        commonMain.dependencies {
        }
        desktopMain.dependencies {
        }
    }
}
