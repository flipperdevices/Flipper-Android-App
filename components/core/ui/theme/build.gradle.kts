plugins {
    androidCompose
}

dependencies {
    implementation(projects.components.core.ui.res)

    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
}
