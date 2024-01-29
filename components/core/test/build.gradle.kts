plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.core.test"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(libs.junit)
    implementation(libs.timber)
    implementation(libs.roboelectric)
    implementation(libs.kotlin.coroutines)
}
            }
        }
    }
