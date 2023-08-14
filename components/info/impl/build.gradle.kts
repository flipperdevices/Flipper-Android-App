plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.info.impl"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.markdown)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.activityholder)
    implementation(projects.components.core.data)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.flippermockup)
    implementation(projects.components.core.share)

    implementation(projects.components.info.api)
    implementation(projects.components.info.shared)

    implementation(projects.components.updater.api)
    implementation(projects.components.firstpair.api)
    implementation(projects.components.updater.api)
    implementation(projects.components.settings.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bottombar.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.rpcinfo.api)

    // Core deps
    implementation(libs.ktx)
    implementation(libs.annotations)

    implementation(libs.appcompat)

    implementation(libs.tangle.viewmodel.compose)
    implementation(libs.tangle.viewmodel.api)
    anvil(libs.tangle.viewmodel.compiler)

    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.navigation)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.immutable.collections)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)

    implementation(libs.ble)
    implementation(libs.ble.common)
    implementation(libs.ble.scan)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
    testImplementation(libs.kotlin.coroutines.test)
}
