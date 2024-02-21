plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.core.markdown"

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(projects.components.core.ui.res)
            implementation(projects.components.core.ui.theme)

            implementation(libs.annotations)
            implementation(libs.appcompat)

            // Compose
            implementation(libs.compose.ui)
            implementation(libs.compose.tooling)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)

            implementation(libs.flexmark.core)
            api(libs.markdown.renderer)

            // Testing
        }
        androidUnitTest.dependencies {
            implementation(projects.components.core.test)
            implementation(libs.junit)
            implementation(libs.mockito.kotlin)
            implementation(libs.ktx.testing)
            implementation(libs.roboelectric)
            implementation(libs.lifecycle.test)
            implementation(libs.kotlin.coroutines.test)
        }
    }
}
