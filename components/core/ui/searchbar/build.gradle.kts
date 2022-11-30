plugins {
    id("flipper.lint")
    id("flipper.android-compose")
}

dependencies {
    implementation(projects.components.core.ui.res)
    implementation(projects.components.core.ui.theme)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)

}