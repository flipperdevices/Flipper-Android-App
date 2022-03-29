plugins {
    androidCompose
}

dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ktx)
    implementation(projects.components.core.navigation)

    implementation(libs.cicerone)

    implementation(libs.annotations)
    implementation(libs.appcompat)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.image.lottie)
}
