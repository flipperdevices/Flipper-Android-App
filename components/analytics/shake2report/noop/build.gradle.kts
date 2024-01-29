plugins {
    id("flipper.multiplatform-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.shake2report.noop"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.analytics.shake2report.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    implementation(libs.compose.ui)

    implementation(libs.bundles.decompose)
}
            }
        }
    }
