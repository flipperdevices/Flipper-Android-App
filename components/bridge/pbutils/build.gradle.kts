plugins {
    id("flipper.multiplatform")
    id("flipper.protobuf")
}

android.namespace = "com.flipperdevices.bridge.pbutils"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.log)
    implementation(libs.kotlin.coroutines)
}
            }
        }
    }
