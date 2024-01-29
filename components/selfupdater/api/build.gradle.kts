plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.selfupdater.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(libs.kotlin.coroutines)
}
            }
        }
    }
