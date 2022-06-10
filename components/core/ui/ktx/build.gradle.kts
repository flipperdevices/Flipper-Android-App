plugins {
    androidCompose
}

dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ui.res)

    implementation(libs.cicerone)
    implementation(projects.components.core.navigation)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.image.lottie)
}
