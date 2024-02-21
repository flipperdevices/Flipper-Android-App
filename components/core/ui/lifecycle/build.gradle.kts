plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.core.ui.lifecycle"

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(projects.components.core.ktx)
            implementation(projects.components.core.log)
            implementation(projects.components.bridge.service.api)

            // Compose
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)

            api(libs.decompose)
            implementation(libs.kotlin.coroutines)
            api(libs.essenty.lifecycle)
            implementation(libs.essenty.lifecycle.coroutines)

            implementation(libs.annotations)
        }
    }
}
