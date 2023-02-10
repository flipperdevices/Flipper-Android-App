plugins {
    id("flipper.android-compose")
}

dependencies {
    implementation(projects.components.deeplink.api)
    implementation(projects.components.bridge.dao.api)
    implementation(projects.components.core.ui.navigation)

    implementation(libs.compose.ui)
    implementation(libs.compose.navigation)
}
