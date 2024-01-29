plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.bottombar.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(libs.compose.ui)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
}
            }
        }
    }
