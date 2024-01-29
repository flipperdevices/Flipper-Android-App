plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.wearable.sync.handheld.noop"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.wearable.sync.handheld.api)

    implementation(projects.components.core.di)
}
            }
        }
    }
