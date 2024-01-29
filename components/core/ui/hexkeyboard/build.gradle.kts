plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.core.ui.hexkeyboard"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.data)

    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.constraint)
}
            }
        }
    }
