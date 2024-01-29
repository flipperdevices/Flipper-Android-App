plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.singleactivity.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.deeplink.api)
}
            }
        }
    }
