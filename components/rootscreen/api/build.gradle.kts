plugins {
    id("flipper.android-compose")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.rootscreen.api"

dependencies {
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.updater.api)

    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.decompose)
}