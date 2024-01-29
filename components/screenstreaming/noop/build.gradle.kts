plugins {
    id("flipper.multiplatform-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.screenstreaming.noop"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.screenstreaming.api)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.compose.ui)
    implementation(libs.bundles.decompose)

    implementation(projects.components.core.di)
}
            }
        }
    }
