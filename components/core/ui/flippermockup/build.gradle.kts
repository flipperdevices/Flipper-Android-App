plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.core.ui.flippermockup"

dependencies {
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

    implementation(projects.components.core.preference)
}
