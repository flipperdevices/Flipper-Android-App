plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.wearable.sync.wear.api"

dependencies {
    implementation(projects.components.core.ui.decompose)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.decompose)

    implementation(projects.components.wearable.wearrootscreen.api)
}
