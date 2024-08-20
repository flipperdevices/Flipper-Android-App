plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.remotecontrols.grid.remote.api"

dependencies {
    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.remoteControls.grid.main.api)
    implementation(projects.components.keyedit.api)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}
