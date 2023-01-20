plugins {
    id("flipper.android-compose")
    id("kotlin-parcelize")
}

dependencies {
    implementation(libs.cicerone)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)

    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
}
