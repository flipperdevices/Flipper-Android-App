plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.infrared.api"

dependencies {
    implementation(projects.components.core.ui.navigation)
    implementation(projects.components.bridge.dao.api)
}
