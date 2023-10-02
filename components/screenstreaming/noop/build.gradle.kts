plugins {
    id("flipper.android-lib")
    id("flipper.anvil")
}

android.namespace = "com.flipperdevices.screenstreaming.noop"

dependencies {
    implementation(projects.components.screenstreaming.api)
    implementation(projects.components.core.ui.navigation)
    implementation(libs.compose.navigation)
    implementation(projects.components.core.di)
}
