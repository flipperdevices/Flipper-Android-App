plugins {
    id("flipper.android-compose")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
}

dependencies {
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.bridge.synchronization.api)
    implementation(projects.components.core.ui.navigation)

    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)

    implementation(libs.kotlin.serialization.json)
}
