plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.shake2report.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bridge.api)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.kotlin.coroutines)

    implementation(libs.decompose)
}
            }
        }
    }
