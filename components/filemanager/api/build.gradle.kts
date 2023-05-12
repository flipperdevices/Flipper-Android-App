plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.filemanager.api"

dependencies {
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.deeplink.api)

    implementation(libs.compose.ui)
}
