plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.notification.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.decompose)

    // Compose
    implementation(libs.compose.ui)
}
            }
        }
    }
