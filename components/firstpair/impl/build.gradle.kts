plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.firstpair.impl"

dependencies {
    implementation(projects.components.firstpair.api)
    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.markdown)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.dialog)

    implementation(projects.components.deeplink.api)
    implementation(projects.components.singleactivity.api)

    // BLE
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    implementation(libs.appcompat)

    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.swipetorefresh)
    implementation(libs.compose.activity)
    implementation(libs.bundles.decompose)
    implementation(libs.lifecycle.compose)

    implementation(libs.ktx)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(projects.components.core.buildKonfig)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
    testImplementation(libs.kotlin.coroutines.test)
}
