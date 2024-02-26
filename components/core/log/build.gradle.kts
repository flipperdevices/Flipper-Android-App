plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.core.log"

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {
            implementation(libs.timber)
        }
    }
}
