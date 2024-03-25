plugins {
    id("flipper.android-lib")
}

android.namespace = "com.flipperdevices.bridge.connection.ble.api"

dependencies {
    implementation(projects.components.bridge.connection.common.api)
    implementation(libs.kotlin.coroutines)
}
