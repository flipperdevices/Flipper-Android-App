plugins {
    id("flipper.lint")
    id("flipper.android-compose")
}

dependencies {
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bridge.dao.api)
    implementation(libs.compose.ui)
    implementation(libs.compose.navigation)
    implementation(projects.components.core.ui.navigation)
}
