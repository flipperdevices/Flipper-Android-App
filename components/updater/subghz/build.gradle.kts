plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.updater.subghz"

dependencies {
    implementation(projects.components.updater.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.service.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.data)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.storage)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.analytics.metric.api)

    implementation(libs.lifecycle.runtime.ktx)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    // Testing
    testImplementation(projects.components.core.buildKonfig)
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(projects.components.updater.downloader)
    testImplementation(libs.ktor.client)
    testImplementation(libs.ktor.negotiation)
    testImplementation(libs.ktor.serialization)
    testImplementation(libs.ktor.mock)
    testImplementation(libs.kotlin.serialization.json)
    testImplementation(libs.kotlin.immutable.collections)
}
