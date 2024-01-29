plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.filemanager.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.deeplink.api)

    implementation(projects.components.core.ui.decompose)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}
            }
        }
    }
