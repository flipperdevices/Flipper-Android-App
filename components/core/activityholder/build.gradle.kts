plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.core.activityholder"

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.appcompat)
            }
        }
    }
}
