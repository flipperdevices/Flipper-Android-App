plugins {
    id("flipper.multiplatform")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.deeplink.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ktx)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.compose.ui)

    implementation(libs.annotations)
    implementation(libs.appcompat)
}
            }
        }
    }
