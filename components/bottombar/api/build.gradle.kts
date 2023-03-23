plugins {
    id("flipper.android-compose")
}

dependencies {
    implementation(libs.compose.ui)
    implementation(projects.components.deeplink.api)
    implementation(projects.components.core.ui.navigation)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
}
