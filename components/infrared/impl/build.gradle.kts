plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.infrared.impl"

dependencies {
    implementation(projects.components.infrared.api)
    implementation(projects.components.keyscreen.api)
    implementation(projects.components.keyscreen.shared)
    implementation(projects.components.keyemulate.api)
    implementation(projects.components.keyparser.api)
    implementation(projects.components.keyedit.api)
    implementation(projects.components.share.api)

    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.synchronization.api)

    implementation(projects.components.remoteControls.grid.saved.api)

    implementation(projects.components.core.di)
    implementation(projects.components.core.log)
    implementation(projects.components.core.data)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.tabswitch)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.dialog)
    implementation(projects.components.core.ui.lifecycle)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.placeholder)
    implementation(libs.bundles.decompose)

    // ViewModel
    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.immutable.collections)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.ktx.testing)
    testImplementation(libs.roboelectric)
    testImplementation(libs.lifecycle.test)
    testImplementation(libs.kotlin.coroutines.test)
}
