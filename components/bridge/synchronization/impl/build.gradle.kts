plugins {
    id("flipper.multiplatform")
    id("flipper.multiplatform-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
    id("kotlin-kapt")
}

android.namespace = "com.flipperdevices.bridge.synchronization.impl"
anvil.generateDaggerFactories.set(false) // DaggerTaskSynchronizationComponent

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(projects.components.bridge.synchronization.api)

            implementation(projects.components.core.di)
            implementation(projects.components.core.ktx)
            implementation(projects.components.core.log)
            implementation(projects.components.core.data)
            implementation(projects.components.core.preference)
            implementation(projects.components.core.progress)
            implementation(projects.components.core.ui.lifecycle)

            implementation(projects.components.bridge.api)
            implementation(projects.components.bridge.pbutils)
            implementation(projects.components.bridge.service.api)
            implementation(projects.components.bridge.dao.api)
            implementation(projects.components.bridge.rpc.api)

            implementation(projects.components.wearable.sync.handheld.api)
            implementation(projects.components.nfc.mfkey32.api)

            implementation(projects.components.analytics.shake2report.api)
            implementation(projects.components.analytics.metric.api)

            // Compose
            implementation(libs.compose.ui)
            implementation(libs.compose.tooling)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material)

            implementation(libs.lifecycle.runtime.ktx)

            implementation(libs.kotlin.serialization.json)

            // Dagger deps

            // Testing
        }
        androidUnitTest.dependencies {
            implementation(projects.components.core.test)
            implementation(libs.junit)
            implementation(libs.mockk)
            implementation(libs.ktx.testing)
            implementation(libs.roboelectric)
            implementation(libs.lifecycle.test)
            implementation(libs.kotlin.coroutines.test)
        }
    }
}
dependencies {
    implementation(libs.dagger)
    kapt(libs.dagger.kapt)
}
