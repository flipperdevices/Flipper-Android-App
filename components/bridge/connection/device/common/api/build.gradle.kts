plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.device.common.api"

dependencies {
    implementation(projects.components.bridge.connection.feature.common.api)
}
