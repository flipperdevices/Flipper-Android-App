plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

androidDependencies {
    implementation(projects.components.core.ui.theme)
    implementation(projects.components.core.ui.ktx)

    // Compose
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.wear.material)
    implementation(libs.horologist.layout)
}
