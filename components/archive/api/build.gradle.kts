plugins {
    id("flipper.multiplatform-compose")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.archive.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.deeplink.api)

    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.decompose)

    implementation(libs.kotlin.serialization.json)
}
            }
        }
    }
