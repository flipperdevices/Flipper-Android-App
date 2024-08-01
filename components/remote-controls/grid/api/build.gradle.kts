plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.remotecontrols.grid.api"

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(projects.components.deeplink.api)

    implementation(projects.components.core.ui.decompose)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}
