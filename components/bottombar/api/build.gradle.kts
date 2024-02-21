plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.bottombar.api"

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(libs.compose.ui)
            implementation(projects.components.deeplink.api)
            implementation(projects.components.core.ui.decompose)

            implementation(libs.decompose)

            // Testing
        }
        androidUnitTest.dependencies {
            implementation(projects.components.core.test)
            implementation(libs.junit)
            implementation(libs.mockito.kotlin)
        }
    }
}
