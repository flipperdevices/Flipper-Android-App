plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
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
    implementation(projects.components.core.ui.navigation)
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
    implementation(libs.compose.navigation)
    implementation(libs.lifecycle.compose)

    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)

    implementation(libs.ktx)
    implementation(libs.ktx.fragment)
}
