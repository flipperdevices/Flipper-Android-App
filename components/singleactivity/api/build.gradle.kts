plugins {
    id("flipper.lint")
    id("androidLibrary")
}

dependencies {
    implementation(projects.components.deeplink.api)
    implementation(libs.cicerone)
}
