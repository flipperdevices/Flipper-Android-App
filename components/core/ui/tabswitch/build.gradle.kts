plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.tabswitch"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}
            }
        }
    }
