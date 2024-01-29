plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.faphub.installation.stateprovider.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.data)

    implementation(projects.components.faphub.dao.api)
    implementation(projects.components.faphub.installation.manifest.api)

    implementation(libs.kotlin.coroutines)
    implementation(libs.annotations)

    implementation(libs.compose.ui)
}
            }
        }
    }
