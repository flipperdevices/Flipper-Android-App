plugins {
    id("flipper.multiplatform-compose")
}

android.namespace = "com.flipperdevices.core.ui.lifecycle"

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.components.core.ktx)
            implementation(projects.components.core.log)

            // Compose
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)

            api(libs.decompose)
            implementation(libs.kotlin.coroutines)
            api(libs.essenty.lifecycle)
            implementation(libs.essenty.lifecycle.coroutines)
        }
        androidMain.dependencies {
            implementation(projects.components.bridge.service.api)
            implementation(libs.annotations)
        }
    }
}
