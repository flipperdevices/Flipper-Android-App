plugins {
    id("flipper.lint")
    id("flipper.android-compose")
}

dependencies {
    implementation(libs.cicerone)

    implementation(libs.compose.ui)

    // Testing
    testImplementation(projects.components.core.test)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
}
