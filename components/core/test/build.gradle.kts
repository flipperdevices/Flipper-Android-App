plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.core.test"

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.coroutines)
            implementation(libs.mockk)

            implementation(projects.components.core.ui.lifecycle)
        }

        androidMain.dependencies {
            implementation(libs.junit)
            implementation(libs.timber)
            implementation(libs.roboelectric)
        }
    }
}
