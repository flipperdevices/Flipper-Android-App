plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.archive.shared"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.keyparser.api)

    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.immutable.collections)
}
            }
        }
    }
