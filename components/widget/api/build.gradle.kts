plugins {
    id("flipper.android-lib")
}

dependencies {
    implementation(libs.cicerone)

    implementation(projects.components.core.ui.navigation)
}
