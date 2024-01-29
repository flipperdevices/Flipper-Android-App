plugins {
    id("flipper.multiplatform-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.wearrootscreen.impl"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.wearable.wearrootscreen.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.wearable.emulate.wear.api)
    implementation(projects.components.wearable.sync.wear.api)
    implementation(projects.components.bridge.dao.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.bundles.decompose)
}
            }
        }
    }
