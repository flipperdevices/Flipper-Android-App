plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.bridge.service.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bridge.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(libs.essenty.lifecycle)
}
            }
        }
    }
