plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.bridge.rpcinfo.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.data)
    implementation(projects.components.core.ktx)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)

    implementation(libs.kotlin.immutable.collections)
    implementation(libs.kotlin.coroutines)
}
            }
        }
    }
