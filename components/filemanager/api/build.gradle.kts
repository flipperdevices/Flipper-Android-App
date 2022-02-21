plugins {
    androidCompose
}

dependencies {
    implementation(projects.components.deeplink.api)

    implementation(libs.cicerone)
    implementation(libs.compose.ui)
}
