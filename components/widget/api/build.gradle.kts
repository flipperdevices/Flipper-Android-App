plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.widget.api"

dependencies {
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.deeplink.api)
}
