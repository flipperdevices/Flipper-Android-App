plugins {
    id("flipper.lint")
    id("flipper.android-compose")
}

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.cicerone)

    // Compose
    implementation(libs.compose.ui)
}
