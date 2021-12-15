plugins {
    androidCompose
}

dependencies {
    implementation(project(":components:deeplink:api"))

    implementation(libs.compose.ui)
}
