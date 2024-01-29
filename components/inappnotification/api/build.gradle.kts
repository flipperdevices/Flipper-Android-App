plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.inappnotification.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    // Compose
    implementation(libs.compose.ui)
}
            }
        }
    }
