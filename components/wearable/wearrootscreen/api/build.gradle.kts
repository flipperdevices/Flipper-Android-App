plugins {
    id("flipper.android-compose")
    id("kotlinx-serialization")
}

android.namespace = "com.flipperdevices.wearrootscreen.api"

dependencies {
    implementation(libs.metro.utils.annotations)
    implementation(projects.components.core.ui.decompose)

    implementation(projects.components.bridge.dao.api)

    implementation(libs.compose.ui)
    implementation(libs.bundles.decompose)
}
