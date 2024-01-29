plugins {
    id("flipper.multiplatform")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.keyedit.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.decompose)
}
            }
        }
    }
