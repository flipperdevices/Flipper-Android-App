plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.remotecontrols.setup.api"

dependencies {
    implementation(projects.components.deeplink.api)

    implementation(projects.components.core.ui.decompose)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}
