plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.wearable.core.ui.theme"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.wear.material)
}
            }
        }
    }
