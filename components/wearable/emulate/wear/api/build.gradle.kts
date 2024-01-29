plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.wearable.emulate.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.bridge.dao.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.wear.gms)

    implementation(libs.decompose)
}
            }
        }
    }
