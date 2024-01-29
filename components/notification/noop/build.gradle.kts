plugins {
    id("flipper.multiplatform-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.notification.noop"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(projects.components.core.di)

    implementation(projects.components.notification.api)
    implementation(projects.components.inappnotification.api)

    // Compose
    implementation(libs.compose.ui)

    implementation(libs.kotlin.coroutines)
    implementation(libs.decompose)
}
            }
        }
    }
