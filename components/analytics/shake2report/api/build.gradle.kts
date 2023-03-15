plugins {
    id("flipper.android-lib")
}

dependencies {
    implementation(projects.components.bridge.api)
    implementation(projects.components.core.ui.navigation)
}
