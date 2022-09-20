plugins {
    id("flipper.lint")
    id("flipper.android-compose")
    id("kotlin-parcelize")
}

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.cicerone)

    // Compose
    implementation(libs.compose.ui)
}
