plugins {
    id("flipper.multiplatform-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.keyscreen.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.keyparser.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.decompose)

    implementation(projects.components.keyemulate.api)
}
            }
        }
    }
