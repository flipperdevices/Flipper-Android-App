plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.bridge.synchronization.api"

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.compose.ui)
    implementation(libs.decompose)
}
