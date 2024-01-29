plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.wearable.sync.handheld.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bridge.dao.api)
}
            }
        }
    }
