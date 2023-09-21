plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.wearable.core.ui.theme"

dependencies {
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.wear.material)
}
