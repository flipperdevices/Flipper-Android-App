plugins {
    id("flipper.android-compose")
}

dependencies {
    // Navigation
    implementation(libs.compose.navigation)
    implementation(projects.components.core.ui.navigation)

    implementation(projects.components.bridge.dao.api)
}
