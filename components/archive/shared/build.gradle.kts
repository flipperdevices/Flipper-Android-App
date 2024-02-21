plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.archive.shared"

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(projects.components.bridge.dao.api)
            implementation(projects.components.keyparser.api)

            implementation(projects.components.core.ui.res)
            implementation(projects.components.core.ui.ktx)
            implementation(projects.components.core.ui.theme)

            // Compose
            implementation(libs.compose.ui)
            implementation(libs.compose.tooling)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)

            // Testing
        }
        androidUnitTest.dependencies {
            implementation(libs.junit)
            implementation(libs.kotlin.immutable.collections)
        }
    }
}
