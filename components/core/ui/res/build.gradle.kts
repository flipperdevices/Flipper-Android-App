plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.core.ui.res"


    kotlin {
        sourceSets {
            commonMain.dependencies {

            }
            androidMain.dependencies {
                dependencies {
    implementation(libs.appcompat)
}
            }
        }
    }
