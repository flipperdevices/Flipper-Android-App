plugins {
    id("flipper.lint")
    id("flipper.android-compose")
}

dependencies {
    implementation(projects.components.bottombar.api)
    implementation(libs.compose.ui)
}
