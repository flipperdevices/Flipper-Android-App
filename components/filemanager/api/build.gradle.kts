plugins {
    androidLibrary
}

dependencies {
    implementation(projects.components.deeplink.api)

    implementation(libs.cicerone)
}
