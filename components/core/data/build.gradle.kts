plugins {
    id("flipper.multiplatform")
}

android.namespace = "com.flipperdevices.core.data"

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(libs.kotlin.immutable.collections)
            implementation(libs.compose.ui)
        }
        androidUnitTest.dependencies {
            implementation(projects.components.core.test)
            implementation(libs.junit)
            implementation(libs.mockito.kotlin)
            implementation(libs.ktx.testing)
        }
    }
}
