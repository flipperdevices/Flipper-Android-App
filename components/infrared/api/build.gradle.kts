plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.infrared.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.bridge.dao.api)

    implementation(libs.decompose)
}
            }
        }
    }
