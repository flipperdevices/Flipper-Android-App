plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.wearable.sync.wear.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.ui.decompose)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.decompose)

    implementation(projects.components.wearable.wearrootscreen.api)
}
            }
        }
    }
