plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.faphub.maincard.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.decompose)
}
            }
        }
    }
