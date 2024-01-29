plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.metric.noop"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.analytics.metric.api)

    implementation(projects.components.core.di)
}
            }
        }
    }
