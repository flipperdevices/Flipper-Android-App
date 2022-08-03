plugins {
    id("androidLibrary")
}

dependencies {
    implementation(libs.cicerone)

    implementation(projects.components.bridge.api)
}
