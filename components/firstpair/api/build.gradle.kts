plugins {
    id("flipper.android-lib")
}

dependencies {
    implementation(projects.components.core.ui.navigation)

    implementation(libs.cicerone)
}
