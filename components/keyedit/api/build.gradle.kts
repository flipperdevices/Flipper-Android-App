plugins {
    id("flipper.android-lib")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.preference)
    implementation(projects.components.core.ui.navigation)

    implementation(libs.cicerone)

    implementation(libs.kotlin.serialization.json)
}
