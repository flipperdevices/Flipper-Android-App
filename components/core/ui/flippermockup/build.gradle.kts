plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.core.ui.flippermockup"


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
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(projects.components.core.preference)
}
            }
        }
    }
