plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.wear.material)
}
