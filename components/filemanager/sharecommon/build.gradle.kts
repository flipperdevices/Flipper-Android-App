plugins {
    androidCompose
}

dependencies {
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ktx)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}
