plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.core.di"

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.dagger)
            }
        }
    }
}
