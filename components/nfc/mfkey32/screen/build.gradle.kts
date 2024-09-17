plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.nfc.mfkey32.screen"

dependencies {
    implementation(projects.components.nfc.mfkey32.api)
    implementation(projects.components.nfc.tools.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.log)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.storage)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.flippermockup)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.markdown)
    implementation(projects.components.core.progress)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.rpc.api)

    implementation(projects.components.analytics.metric.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bottombar.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.bundles.decompose)

    implementation(libs.kotlin.immutable.collections)

    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
}
