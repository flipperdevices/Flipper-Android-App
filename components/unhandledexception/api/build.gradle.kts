plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.unhandledexception.api"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(libs.compose.ui)
}
            }
        }
    }
