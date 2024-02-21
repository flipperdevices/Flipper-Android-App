plugins {
    id("flipper.multiplatform-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.keyemulate.impl"

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(projects.components.keyemulate.api)
            implementation(projects.components.keyparser.api)
            implementation(projects.components.screenstreaming.api)

            implementation(projects.components.core.di)
            implementation(projects.components.core.log)
            implementation(projects.components.core.ktx)
            implementation(projects.components.core.data)
            implementation(projects.components.core.ui.res)
            implementation(projects.components.core.ui.ktx)
            implementation(projects.components.core.ui.theme)
            implementation(projects.components.core.ui.dialog)
            implementation(projects.components.core.ui.lifecycle)

            implementation(projects.components.bridge.api)
            implementation(projects.components.bridge.service.api)
            implementation(projects.components.bridge.dao.api)
            implementation(projects.components.bridge.pbutils)
            implementation(projects.components.bridge.synchronization.api)

            implementation(projects.components.rootscreen.api)

            // Compose
            implementation(libs.compose.ui)
            implementation(libs.compose.tooling)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)
            implementation(libs.compose.placeholder)
            implementation(libs.bundles.decompose)

            implementation(libs.image.lottie)

            implementation(libs.appcompat)

            implementation(libs.lifecycle.compose)
            implementation(libs.lifecycle.viewmodel.ktx)
            implementation(libs.lifecycle.runtime.ktx)

            // Testing
        }
        androidUnitTest.dependencies {
            implementation(projects.components.core.test)
            implementation(libs.junit)
            implementation(libs.kotlin.coroutines.test)
            implementation(libs.roboelectric)
            implementation(libs.ktx.testing)
            implementation(libs.mockk)
        }
    }
}
