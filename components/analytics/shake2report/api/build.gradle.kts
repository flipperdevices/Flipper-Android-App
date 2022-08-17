plugins {
    id("flipper.lint")
    id("androidLibrary")
}

dependencies {
    implementation(libs.cicerone)

    implementation(projects.components.bridge.api)
}
