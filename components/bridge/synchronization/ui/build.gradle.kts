plugins {
    id("flipper.multiplatform-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge.synchronization.ui"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.bridge.dao.api)

    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.lifecycle.compose)
}
            }
        }
    }
