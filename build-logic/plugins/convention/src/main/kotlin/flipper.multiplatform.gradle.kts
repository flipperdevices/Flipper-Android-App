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
        val commonMain by getting

        /**
         * We shouldn't create sources, which is named as original targets sourcesets.
         *
         * As an example - jvm() target will create sourceSet jvmMain - and we would have conflicts
         * with our create jvmMain sourceSet
         *
         * This is the reason to name it `jvmSharedMain`
         */
        val jvmSharedMain by creating {
            dependsOn(commonMain)
        }

        val androidMain by getting {
            dependsOn(jvmSharedMain)
        }

        val desktopMain by getting {
            dependsOn(jvmSharedMain)
        }

        androidMain.dependencies {
        }
        commonMain.dependencies {
        }
        desktopMain.dependencies {
        }

        // Testing
        val commonTest by getting

        val jvmSharedTest by creating {
            dependsOn(commonTest)
        }

        @Suppress("UnusedPrivateProperty")
        val androidUnitTest by getting {
            dependsOn(jvmSharedTest)
        }

        @Suppress("UnusedPrivateProperty")
        val desktopTest by getting {
            dependsOn(jvmSharedTest)
        }
    }
}
