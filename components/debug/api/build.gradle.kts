plugins {
    id("flipper.lint")
    id("flipper.android-compose")
}

dependencies {
    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}
