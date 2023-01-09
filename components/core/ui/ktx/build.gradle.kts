plugins {
    id("flipper.android-compose")
}

dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)

    implementation(libs.cicerone)
    implementation(projects.components.core.navigation)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.placeholder)
    implementation(libs.compose.coil)
    implementation(libs.image.lottie)
}
