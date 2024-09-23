plugins {
    id("flipper.android-compose")

    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.archive.impl"

dependencies {
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

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.pager)
    implementation(libs.compose.pager.indicators)
    implementation(libs.compose.swipetorefresh)
    implementation(libs.bundles.decompose)
    implementation(libs.bundles.essenty)

    implementation(libs.kotlin.immutable.collections)

    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.compose)
}
