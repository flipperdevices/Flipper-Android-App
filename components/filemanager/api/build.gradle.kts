plugins {
    id("androidCompose")
}

dependencies {
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.deeplink.api)

    implementation(libs.cicerone)
    implementation(libs.compose.ui)
}
