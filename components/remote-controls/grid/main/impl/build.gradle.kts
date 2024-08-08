plugins {
    id("flipper.android-compose")
    id("flipper.anvil")
    id("kotlinx-serialization")
}
android.namespace = "com.flipperdevices.remotecontrols.grid.main.impl"

dependencies {
    implementation(projects.components.core.di)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.log)

    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.remoteControls.grid.main.api)
    implementation(projects.components.remoteControls.grid.createControl.api)
    implementation(projects.components.remoteControls.grid.remote.api)
    implementation(projects.components.remoteControls.grid.saved.api)
    implementation(projects.components.keyedit.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)

    implementation(libs.bundles.decompose)
}
