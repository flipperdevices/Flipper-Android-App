plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.faphub.utils"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.data)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.rpc.api)

    implementation(libs.kotlin.coroutines)

    implementation(libs.dagger)
}
            }
        }
    }
