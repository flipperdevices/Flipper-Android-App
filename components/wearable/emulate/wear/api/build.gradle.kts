plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.wearable.emulate.api"

dependencies {
    implementation(projects.components.core.ui.navigation)
}
