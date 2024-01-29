plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.bridge.rpc.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.progress)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)

    implementation(libs.kotlin.coroutines)
}
            }
        }
    }
