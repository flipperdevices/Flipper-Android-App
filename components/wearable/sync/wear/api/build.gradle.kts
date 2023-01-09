plugins {
        id("flipper.android-compose")
}

dependencies {
    implementation(projects.components.core.ui.navigation)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
}
