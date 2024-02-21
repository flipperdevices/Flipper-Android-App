plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.bridge"

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(projects.components.core.log)
            implementation(projects.components.core.ktx)
            implementation(projects.components.core.di)
            implementation(projects.components.core.preference)
            implementation(projects.components.core.data)
            implementation(projects.components.analytics.shake2report.api)
            implementation(projects.components.analytics.metric.api)

            implementation(projects.components.bridge.api)
            implementation(projects.components.bridge.service.api)
            implementation(projects.components.bridge.pbutils)
            implementation(projects.components.bridge.rpcinfo.api)

            implementation(libs.kotlin.coroutines)
            implementation(libs.kotlin.immutable.collections)
            implementation(libs.annotations)
            implementation(libs.ktx)
            implementation(libs.appcompat)

            implementation(libs.ble.scan)
            implementation(libs.ble)
            implementation(libs.ble.common)

            implementation(libs.fastutil)

            // Testing
        }
        androidUnitTest.dependencies {
            implementation(projects.components.core.test)
            implementation(libs.junit)
            implementation(libs.mockk)
            implementation(libs.mockito.kotlin)
            implementation(libs.ktx.testing)
            implementation(libs.roboelectric)
            implementation(libs.lifecycle.test)
            implementation(libs.kotlin.coroutines.test)
        }
    }
}
