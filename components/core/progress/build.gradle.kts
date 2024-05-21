plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.core.progress"

kotlin {
    sourceSets {
        commonTest {
            dependencies {
                // Testing
                implementation(libs.junit)
                implementation(libs.mockk)
                implementation(libs.kotlin.coroutines.test)
            }
        }
    }
}
