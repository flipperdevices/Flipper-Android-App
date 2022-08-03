plugins {
    id("androidLibrary")
}

dependencies {
    implementation(projects.components.bridge.api)

    implementation(libs.annotations)
    implementation(libs.appcompat)
}
