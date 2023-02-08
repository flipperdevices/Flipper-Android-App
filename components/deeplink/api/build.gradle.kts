plugins {
    id("flipper.android-lib")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

dependencies {
    implementation(projects.components.bridge.dao.api)

    implementation(libs.kotlin.serialization.json)
    implementation(libs.compose.navigation)

    implementation(libs.annotations)
    implementation(libs.appcompat)
    implementation(libs.cicerone)
}
