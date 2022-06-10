plugins {
    androidCompose
}

dependencies {
    implementation(projects.components.core.ui.res)

    implementation(projects.components.bridge.dao.api)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.constraint)
}
