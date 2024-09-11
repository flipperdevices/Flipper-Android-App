plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.remotecontrols.core.ui"

dependencies {
    implementation(projects.components.core.log)

    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.dialog)

    implementation(projects.components.remoteControls.apiBackend)
    implementation(projects.components.remoteControls.apiBackendFlipper)
    implementation(projects.components.remoteControls.coreModel)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.tooling)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.placeholder)

    implementation(libs.bundles.decompose)
}
