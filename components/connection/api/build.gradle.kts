plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.connection.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bottombar.api)
    implementation(libs.compose.ui)

    implementation(libs.decompose)
}
            }
        }
    }
