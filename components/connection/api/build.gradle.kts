plugins {
    id("flipper.lint")
    id("androidCompose")
}

dependencies {
    implementation(projects.components.bottombar.api)
    implementation(libs.compose.ui)
}
