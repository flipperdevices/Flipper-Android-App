plugins {
    id("flipper.multiplatform")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.faphub.report.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)

    implementation(libs.kotlin.serialization.json)
}
            }
        }
    }
