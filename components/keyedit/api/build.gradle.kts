plugins {
    androidCompose
}

dependencies {
    implementation(projects.components.core.ui)

    implementation(projects.components.bridge.dao.api)

    implementation(libs.cicerone)

    // Compose
    implementation(libs.compose.ui)
}
