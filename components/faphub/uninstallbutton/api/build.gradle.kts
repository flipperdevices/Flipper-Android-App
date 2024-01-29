plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.faphub.uninstallbutton.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(libs.compose.ui)

    implementation(projects.components.faphub.dao.api)
}
            }
        }
    }
