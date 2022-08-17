plugins {
    id("flipper.lint")
    id("androidLibrary")
    id("kotlin-parcelize")
}

dependencies {
    implementation(libs.cicerone)
    implementation(projects.components.bridge.dao.api)
}
