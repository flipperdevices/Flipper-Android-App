plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.nfc.mfkey32.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(libs.bundles.decompose)

    implementation(projects.components.bridge.api)

    implementation(libs.kotlin.coroutines)
}
            }
        }
    }
