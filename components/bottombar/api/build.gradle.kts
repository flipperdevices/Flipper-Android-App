plugins {
    androidCompose
}

dependencies {
    implementation(libs.cicerone)

    implementation(libs.compose.ui)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
}
