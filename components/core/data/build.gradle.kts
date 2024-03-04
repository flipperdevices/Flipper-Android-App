plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.core.data"

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.immutable.collections)
            implementation(libs.compose.ui)
        }

        commonTest.dependencies {
            implementation(libs.junit)
            implementation(libs.mockito.kotlin)
            implementation(libs.ktx.testing)
        }
    }
}
