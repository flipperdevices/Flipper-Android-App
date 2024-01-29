plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.selfupdater.unknown"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.selfupdater.api)

    implementation(projects.components.core.di)
}
            }
        }
    }
