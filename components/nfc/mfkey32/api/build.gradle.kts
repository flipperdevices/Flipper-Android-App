plugins {
    id("flipper.lint")
    id("flipper.android-lib")
}

dependencies {
    implementation(projects.components.core.ui.navigation)

    implementation(projects.components.bridge.api)

    implementation(libs.kotlin.coroutines)
}
