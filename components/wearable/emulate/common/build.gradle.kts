plugins {
    id("flipper.multiplatform")
    id("flipper.protobuf")
}

android.namespace = "com.flipperdevices.wearable.emulate.common"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.bridge.pbutils)

    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.play.services)
    implementation(libs.wear.gms)
}
            }
        }
    }
