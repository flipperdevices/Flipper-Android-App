plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.hub.api"

dependencies {
    implementation(projects.components.core.ui.navigation)

    implementation(libs.kotlin.coroutines)
}
