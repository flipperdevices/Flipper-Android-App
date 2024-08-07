plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}
android.namespace = "com.flipperdevices.remotecontrols.grid.impl"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)

    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.dialog)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.infrared.utils)

    implementation(projects.components.remoteControls.apiBackend)
    implementation(projects.components.remoteControls.coreModel)
    implementation(projects.components.remoteControls.coreUi)
    implementation(projects.components.remoteControls.grid.api)
    implementation(projects.components.remoteControls.setup.api)

    implementation(projects.components.rootscreen.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)

    implementation(libs.kotlin.immutable.collections)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.decompose)

    implementation(libs.bundles.decompose)
}
