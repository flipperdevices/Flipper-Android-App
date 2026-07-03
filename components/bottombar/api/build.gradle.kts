plugins {
    id("flipper.multiplatform-compose")
    id("flipper.multiplatform-dependencies")
}

android.namespace = "com.flipperdevices.bottombar.api"

androidDependencies {
    implementation(projects.components.deeplink.api)
    implementation(projects.components.core.ui.decompose)

    implementation(libs.decompose)

    // Testing
}

dependencies {
    "androidUnitTestImplementation"(projects.components.core.test)
    "androidUnitTestImplementation"(libs.junit)
    "androidUnitTestImplementation"(libs.mockito.kotlin)
}
