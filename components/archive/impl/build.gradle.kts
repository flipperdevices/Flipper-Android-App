plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")

    id("flipper.anvil")
    id("kotlinx-serialization")
}

androidDependencies {
    implementation(projects.components.archive.api)
    implementation(projects.components.archive.shared)

    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.lifecycle)

    implementation(projects.components.connection.api)
    implementation(projects.components.keyscreen.api)
    implementation(projects.components.keyedit.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bottombar.api)
    implementation(projects.components.rootscreen.api)

    implementation(projects.components.remoteControls.main.api)
    implementation(projects.components.remoteControls.grid.saved.api)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.connection.feature.provider.api)
    implementation(projects.components.bridge.connection.feature.common.api)
    implementation(projects.components.bridge.connection.feature.serialspeed.api)
    implementation(projects.components.bridge.connection.transport.common.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.pager)
    implementation(libs.compose.pager.indicators)
    implementation(libs.compose.swipetorefresh)
    implementation(libs.bundles.decompose)
    implementation(libs.bundles.essenty)

    implementation(libs.kotlin.immutable.collections)

    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.lifecycle.compose)
}
