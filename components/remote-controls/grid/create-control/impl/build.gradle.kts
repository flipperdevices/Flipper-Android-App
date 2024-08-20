plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}
android.namespace = "com.flipperdevices.remotecontrols.grid.createcontrol.impl"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)

    implementation(projects.components.core.ui.lifecycle)
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)

    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.rpc.api)
    implementation(projects.components.bridge.service.api)
    implementation(projects.components.bridge.api)
    implementation(projects.components.bridge.pbutils)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.rootscreen.api)

    implementation(projects.components.remoteControls.grid.createControl.api)
    implementation(projects.components.keyedit.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(libs.kotlin.immutable.collections)

    implementation(libs.decompose)

    implementation(libs.bundles.decompose)
}
