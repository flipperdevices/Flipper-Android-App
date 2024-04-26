plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.transport.ble.api"

dependencies {
    implementation(projects.components.bridge.connection.transport.common.api)
    implementation(libs.kotlin.coroutines)
}
