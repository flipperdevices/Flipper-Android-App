plugins {
    androidCompose
}

dependencies {
    implementation(projects.components.updater.api)
    implementation(projects.components.core.ui)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}
