plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.remotecontrols.device.select.api"

dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.deeplink.api)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}
