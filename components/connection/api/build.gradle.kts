plugins {
    id("androidCompose")
}

dependencies {
    implementation(projects.components.bottombar.api)
    implementation(libs.compose.ui)
}
