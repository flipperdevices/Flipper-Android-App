plugins {
    id("flipper.android-compose")
}

dependencies {
    implementation(libs.cicerone)

    implementation(libs.compose.ui)
    implementation(projects.components.deeplink.api)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
}
