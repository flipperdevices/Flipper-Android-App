plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.wearable.setup.api"

dependencies {
    implementation(projects.components.core.ui.navigation)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.wear.material)
    implementation(libs.compose.wear.navigation)
}
