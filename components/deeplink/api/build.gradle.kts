plugins {
    id("flipper.lint")
    id("androidLibrary")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.kotlin.serialization.json)

    implementation(libs.annotations)
    implementation(libs.appcompat)
    implementation(libs.cicerone)
}
