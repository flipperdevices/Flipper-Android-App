plugins {
    id("flipper.multiplatform-compose")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.rootscreen.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.updater.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bridge.dao.api)

    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.decompose)
}
            }
        }
    }
