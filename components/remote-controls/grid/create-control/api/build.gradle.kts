plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.remotecontrols.grid.createcontrol.api"

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.core.ui.decompose)
    implementation(projects.components.keyedit.api)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}
