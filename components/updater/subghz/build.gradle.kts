plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.updater.subghz"

androidDependencies {
    implementation(projects.components.updater.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.data)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.progress)
    implementation(projects.components.core.storage)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.analytics.metric.api)

    implementation(libs.lifecycle.runtime.ktx)

    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.storage.api)
    implementation(projects.components.bridge.connection.feature.getinfo.api)
    implementation(projects.components.bridge.connection.feature.update.api)
    implementation(projects.components.bridge.connection.feature.rpc.api)
    implementation(projects.components.bridge.connection.pbutils)

    // Compose

    // Testing
}

dependencies {
    "androidUnitTestImplementation"(projects.components.core.buildKonfig)
    "androidUnitTestImplementation"(projects.components.core.test)
    "androidUnitTestImplementation"(libs.junit)
    "androidUnitTestImplementation"(libs.mockk)
    "androidUnitTestImplementation"(libs.ktx.testing)
    "androidUnitTestImplementation"(libs.roboelectric)
    "androidUnitTestImplementation"(libs.lifecycle.test)
    "androidUnitTestImplementation"(libs.kotlin.coroutines.test)
    "androidUnitTestImplementation"(projects.components.updater.downloader)
    "androidUnitTestImplementation"(libs.ktor.client)
    "androidUnitTestImplementation"(libs.ktor.negotiation)
    "androidUnitTestImplementation"(libs.ktor.serialization)
    "androidUnitTestImplementation"(libs.ktor.mock)
    "androidUnitTestImplementation"(libs.kotlin.serialization.json)
    "androidUnitTestImplementation"(libs.kotlin.immutable.collections)
}
