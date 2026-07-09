plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
    id("flipper.anvil")
    id("kotlinx-serialization")
}


androidDependencies {
    implementation(projects.components.toolstab.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.analytics.metric.api)

    implementation(libs.appcompat)

    implementation(projects.components.nfc.mfkey32.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bottombar.api)
    implementation(projects.components.rootscreen.api)
    implementation(projects.components.info.shared)
    implementation(projects.components.remoteControls.main.api)

    // Compose
    implementation(libs.lifecycle.compose)

    implementation(libs.bundles.decompose)
    implementation(libs.kotlin.immutable.collections)
}
