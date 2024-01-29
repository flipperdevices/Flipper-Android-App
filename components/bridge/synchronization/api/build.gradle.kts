plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.bridge.synchronization.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}
            }
        }
    }
