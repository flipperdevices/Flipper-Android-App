plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

androidDependencies {
    implementation(projects.components.firstpair.api)
    implementation(projects.components.core.buildKonfig)
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
    implementation(projects.components.firstpair.connection.api)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    implementation(projects.components.bridge.connection.config.api)

    implementation(libs.appcompat)

    implementation(libs.kotlin.immutable.collections)

    // Compose
    implementation(libs.compose.swipetorefresh)
    implementation(libs.compose.activity)
    implementation(libs.bundles.decompose)
    implementation(libs.lifecycle.compose)

    implementation(libs.ktx)

    // Testing
}

androidHostTestDependencies {
    implementation(projects.components.core.test)
    implementation(projects.components.core.buildKonfig)
    implementation(libs.junit)
    implementation(libs.mockk)
    implementation(libs.ktx.testing)
    implementation(libs.roboelectric)
    implementation(libs.lifecycle.test)
    implementation(libs.kotlin.coroutines.test)
}
