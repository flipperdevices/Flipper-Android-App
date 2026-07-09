plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}


androidDependencies {
    implementation(projects.components.deeplink.api)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)

    // Testing
}

androidHostTestDependencies {
    implementation(projects.components.core.test)
    implementation(libs.junit)
    implementation(libs.mockito.kotlin)
}
