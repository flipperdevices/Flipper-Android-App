plugins {
    id("flipper.android-compose")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.rootscreen.api"

dependencies {
    implementation(projects.components.faphub.screenshotspreview.api)

    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.updater.api)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bridge.dao.api)

    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.decompose)
}
