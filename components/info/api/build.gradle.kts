plugins {
    id("flipper.android-lib")
}

dependencies {
    implementation(projects.components.deeplink.api)

    implementation(projects.components.core.ui.navigation)
}
