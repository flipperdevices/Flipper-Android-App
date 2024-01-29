plugins {
    id("flipper.multiplatform-compose")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.wearrootscreen.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.bridge.dao.api)

    implementation(libs.compose.ui)
    implementation(libs.bundles.decompose)
}
            }
        }
    }
