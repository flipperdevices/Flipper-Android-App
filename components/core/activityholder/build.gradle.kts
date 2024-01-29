plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.core.activityholder"


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
