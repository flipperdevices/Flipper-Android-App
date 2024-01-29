plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.service.noop"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.core.di)

    implementation(projects.components.bridge.api)

    implementation(libs.essenty.lifecycle)
}
            }
        }
    }
