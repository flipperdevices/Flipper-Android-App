plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
    id("flipper.protobuf")
}

android.namespace = "com.flipperdevices.core.preference"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.share)

    api(libs.datastore)
}
            }
        }
    }
