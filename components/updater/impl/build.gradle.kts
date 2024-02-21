plugins {
    id("flipper.multiplatform")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.updater.impl"

kotlin {
    sourceSets {
        commonMain.dependencies {
        }
        androidMain.dependencies {

            implementation(projects.components.updater.api)
            implementation(projects.components.updater.subghz)

            implementation(projects.components.bridge.api)
            implementation(projects.components.bridge.pbutils)
            implementation(projects.components.bridge.service.api)
            implementation(projects.components.bridge.rpc.api)
            implementation(projects.components.deeplink.api)

            implementation(projects.components.core.di)
            implementation(projects.components.core.log)
            implementation(projects.components.core.ktx)
            implementation(projects.components.core.progress)
            implementation(projects.components.core.preference)
            implementation(projects.components.core.ui.lifecycle)
            implementation(projects.components.core.ui.res)

            implementation(projects.components.analytics.metric.api)
            implementation(projects.components.faphub.installedtab.api)

            implementation(libs.lifecycle.runtime.ktx)

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
            implementation(projects.components.updater.downloader)
            implementation(libs.ktor.client)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.mock)
            implementation(libs.kotlin.serialization.json)
        }
    }
}
