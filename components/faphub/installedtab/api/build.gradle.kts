plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.faphub.installedtab.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.faphub.dao.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.decompose)
}
            }
        }
    }
